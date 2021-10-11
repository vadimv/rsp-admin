package rsp.admin.crud.components;

import rsp.App;
import rsp.admin.crud.entities.Principal;
import rsp.admin.crud.services.Auth;
import rsp.html.DocumentPartDefinition;
import rsp.page.PageLifeCycle;
import rsp.page.QualifiedSessionId;
import rsp.routing.Route;
import rsp.server.HttpRequest;
import rsp.server.Path;
import rsp.state.UseState;
import rsp.util.data.Tuple2;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static rsp.html.HtmlDsl.*;
import static rsp.routing.RoutingDsl.*;
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

        return new App<>(routes(),
                         this::appRoot).stateToPath(this::stateToPath)
                                       .pageLifeCycle(pageLifeCycle);
    }

    private Route<HttpRequest, AppState> routes() {
        return concat(get(req -> paths(principal(req.deviceId()))),
                      any(notFound()));
    }

    private Route<Path, AppState> paths(Optional<Tuple2<String, Principal>> principal) {
        return concat(path("",  CompletableFuture.completedFuture(new State(principal, Optional.empty()))),
                      path("/login", CompletableFuture.completedFuture(new State(Optional.empty(), Optional.empty()))),
                      path("/:resourceName", resourceName -> resourceList(resourceName, principal)),
                      path( "/:resourceName/:key", (resourceName, key) -> resourceDetails(resourceName, key, principal)));
    }

    private Optional<Tuple2<String, Principal>> principal(Optional<String> deviceId) {
        return deviceId.flatMap(id -> Optional.ofNullable(principals.get(id)).map(p -> new Tuple2<>(id, p)));
    }

    private CompletableFuture<AppState> resourceList(String resourceName, Optional<Tuple2<String, Principal>> principal) {
        return  resource(resourceName).map(resource -> listState(principal, resource))
                .orElse(CompletableFuture.completedFuture(notFound()));
    }

    private CompletableFuture<AppState> listState(Optional<Tuple2<String, Principal>> principal, Resource<?> resource) {
        return resource.initialListState().thenApply(resourceState -> new State(principal, Optional.of(resourceState)));
    }

    private CompletableFuture<AppState> resourceDetails(String resourceName, String key,  Optional<Tuple2<String, Principal>> principal) {
        return  resource(resourceName).map(resource -> listStateWKey(principal, resource, key))
                      .orElse(CompletableFuture.completedFuture(notFound()));
    }

    private CompletableFuture<AppState> listStateWKey(Optional<Tuple2<String, Principal>> principal, Resource<?> resource, String key) {
        return resource.initialListStateWithEdit(key).thenApply(resourceState -> new State(principal, Optional.of(resourceState)));
    }

    private Optional<Resource> resource(String name) {
        return Arrays.stream(resources).filter(r -> name.equals(r.name)).findFirst();
    }

    private NotFoundState notFound() {
        return new NotFoundState();
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
            ctx.eventObject().value("path").flatMap(path -> paths(us.get().principal).apply(Path.of(path.toString())))
                    .ifPresent(state -> state.thenAccept(s -> us.accept((State)s)))),
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
