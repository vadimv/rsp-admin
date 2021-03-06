package rsp.admin.crud.components;

import rsp.App;
import rsp.AppConfig;
import rsp.admin.crud.entities.Principal;
import rsp.admin.crud.services.Auth;
import rsp.dsl.DocumentPartDefinition;
import rsp.page.PageLifeCycle;
import rsp.page.QualifiedSessionId;
import rsp.server.Path;
import rsp.state.UseState;
import rsp.util.data.Tuple2;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static rsp.dsl.Html.*;
import static rsp.state.UseState.readWrite;

public class Admin {
    private final String title;
    private final Resource[] resources;

    private static final Map<String, Principal> principals = new ConcurrentHashMap<>();

    private final Auth auth = new Auth();
    private final PubSub pubSub = new PubSub();

    public Admin(String title, Resource<?>... resources) {
        this.title = title;
        this.resources = resources;
    }

    public App<AppState> app() {

        final PageLifeCycle<AppState> pageLifeCycle = new PageLifeCycle<>() {
            @Override
            public void beforeLivePageCreated(QualifiedSessionId sid, UseState<AppState> useState) {
                pubSub.subscribe(sid.deviceId, sid.sessionId, message -> {
                    synchronized (useState) {
                        useState.accept(useState.cast(State.class).get().withoutPrincipal());
                    }
                });
            }

            @Override
            public void afterLivePageClosed(QualifiedSessionId sid, AppState state) {
                pubSub.unsubscribe(sid.deviceId, sid.sessionId);
            }
        };

        return new App<>(AppConfig.DEFAULT,
                         request -> dispatch(request.deviceId().flatMap(id -> Optional.ofNullable(principals.get(id)).map(p -> new Tuple2<>(id, p))),
                                             request.path),
                         this::stateToPath,
                         pageLifeCycle,
                         this::appRoot);
    }

    private CompletableFuture<? extends AppState> dispatch(Optional<Tuple2<String, Principal>> principal, Path path) {

        final Path.Matcher<AppState> m = new Path.Matcher<AppState>(path, new NotFoundState())
                                          .match((name) -> "login".equals(name),
                                                (name) -> CompletableFuture.completedFuture(new rsp.admin.crud.components.Admin.State(Optional.empty(), Optional.empty())));

        for (Resource<?> resource : resources) {
            final Path.Matcher<? extends AppState> sm = m.match(() -> true,
                                                                () -> CompletableFuture.completedFuture(new State(principal, Optional.empty())))
                                            .match((name) -> name.equals(resource.name),
                                                   (name) -> resource.initialListState().thenApply(resourceState -> new State(principal, Optional.of(resourceState))))
                                            .match((name, key) -> name.equals(resource.name),
                                                   (name, key) -> resource.initialListStateWithEdit(key).thenApply(resourceState -> new State(principal, Optional.of(resourceState))));
            if (sm.isMatch) {
                return sm.state;
            }
        }

        return m.state;
    }

    private AppState error() {
        return new ErrorState();
    }

    private Path stateToPath(AppState appState, Path p) {
        if (appState instanceof State) {
            final var s = (State) appState;
            if (s.principal.isPresent()) {
                return s.currentResource.map(state -> state.details.map(detailsViewState -> Path.of(state.name
                        + "/" + detailsViewState.currentKey.orElse("create")))
                        .orElseGet(() -> Path.of(state.name))).orElse(Path.EMPTY_ABSOLUTE);
            } else {
                return Path.of("login");
            }
        } else if (appState instanceof NotFoundState){
            Path.of("notFound");
        }
        assert appState instanceof ErrorState;
        return Path.of("error");
    }

    private DocumentPartDefinition appRoot(UseState<AppState> us) {
        if (us.isInstanceOf(State.class)) {
            return appRootOk(us.cast(State.class));
        } else if (us.isInstanceOf(NotFoundState.class)) {
            return appRootNotFound();
        } else {
            throw new IllegalStateException();
        }
    }

    private DocumentPartDefinition appRootNotFound() {
        return html(headPlain(title("Not found")),
                    body(h2("404"))).statusCode(404);
    }

    private DocumentPartDefinition appRootOk(UseState<State> us) {
        return html(window().on("popstate",
                                ctx ->
            ctx.eventObject().value("path").ifPresent(path -> dispatch(us.get().principal, Path.of(path.toString()))
                                                                         .thenAccept(s -> {
                                                                             if (s instanceof State) {
                                                                                 us.accept((State) s);
                                                                             } else {
                                                                                 throw new IllegalStateException("Bad path: " + path);
                                                                             }
                                                                         })))
        ,
                    head(title(title + us.get().currentResource.map(r -> ": " + r.title).orElse("")),
                         link(attr("rel", "stylesheet"), attr("href","/res/style.css"))),
                    body(us.get().principal.map(u -> div(div(span(u._2.name),
                                                        a("#", "Logout", on("click", ctx -> {
                                                            principals.remove(ctx.sessionId().deviceId);
                                                            pubSub.publish(ctx.sessionId().deviceId, "logout");
                                                            us.accept(us.get().withoutPrincipal());
                                                         }))),
                                                    new MenuPanel().render(new MenuPanel.State(Arrays.stream(resources).map(r -> new Tuple2<>(r.name, r.title)).collect(Collectors.toList()))),

                                div(of(us.get().currentResource.flatMap(this::findResourceComponent).map(p -> p._2.render(readWrite(() -> p._1,
                                                                                                                            v -> us.accept(us.get().withResource(Optional.of(v)))))).stream()))))

                                .orElse(div(new LoginForm().render(new LoginForm.State(),
                                                               lfs -> auth.authenticate(lfs.userName, lfs.password)
                                                                            .thenAccept(po -> po.ifPresentOrElse(p -> lfs.deviceId.ifPresent(id -> {
                                                                                principals.put(id, p);
                                                                                us.accept(us.get().withPrincipal(new Tuple2<>(lfs.deviceId.get(), p)));
                                                                            }),
                                                                                    () -> us.accept(us.get().withoutPrincipal()))))))
                    ));
    }

    private Optional<Tuple2<Resource.State, Resource>> findResourceComponent(Resource.State resourceState) {
        return Arrays.stream(resources).filter(resource -> resource.name.equals(resourceState.name)).map(component -> new Tuple2<>(resourceState, component)).findFirst();
    }

    interface AppState {
    }

    public static class ErrorState implements AppState {
    }

    public static class NotFoundState implements AppState {
    }

    public static class State implements AppState {
        public final Optional<Tuple2<String, Principal>> principal;
        public final Optional<Resource.State<?>> currentResource;

        public State(Optional<Tuple2<String, Principal>> principal, Optional<Resource.State<?>> currentResource) {
            this.principal = principal;
            this.currentResource = currentResource;
        }

        public State withResource(Optional<Resource.State<?>> currentResource) {
            return new State(this.principal, currentResource);
        }

        public State withPrincipal(Tuple2<String, Principal> principal) {
            return new State(Optional.of(principal), this.currentResource);
        }

        public State withoutPrincipal() {
            return new State(Optional.empty(), this.currentResource);
        }
    }
}
