package rsp.admin.crud.components.main;

import rsp.Component;
import rsp.admin.pubsub.PubSub;
import rsp.admin.auth.Auth;
import rsp.html.DocumentPartDefinition;
import rsp.server.Path;
import rsp.state.UseState;
import rsp.util.data.Tuple2;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static rsp.html.HtmlDsl.*;
import static rsp.html.HtmlDsl.div;
import static rsp.state.UseState.readWrite;

public final class AdminView implements Component<ViewState> {

    private final String title;
    private final AdminRouting routing;
    private final AdminResources resources;
    private final Auth auth;
    private final Principals principals;
    private final PubSub pubSub;
    public AdminView(String title,
                     AdminRouting routing,
                     AdminResources resources,
                     Auth auth,
                     Principals principals,
                     PubSub pubSub) {
        this.title = title;
        this.routing = routing;
        this.resources = resources;
        this.auth = auth;
        this.principals = principals;
        this.pubSub = pubSub;
    }

    @Override
    public DocumentPartDefinition render(UseState<ViewState> us) {
        if (us.get() instanceof ViewState.Success) {
            return appRootOk((ViewState.Success) us.get(), us);
        } else if (us.get() instanceof ViewState.NotFound) {
            return appRootNotFound();
        } else {
            throw new IllegalStateException();
        }
    }

    private DocumentPartDefinition appRootNotFound() {
        return html(headPlain(title("Not found")),
                    body(h2("404"))).statusCode(404);
    }

    private DocumentPartDefinition appRootOk(ViewState.Success s, UseState<ViewState> us) {
        return html(window().on("popstate",
                        ctx ->
                                ctx.eventObject().value("path").flatMap(path -> routing.paths(s.principal).apply(Path.of(path.toString())))
                                        .ifPresent(state -> state.thenAccept(v -> us.accept(v)))),
                head(title(title + s.resourceState.map(r -> ": " + r.title).orElse("")),
                        link(attr("rel", "stylesheet"), attr("href","/res/style.css"))),
                body(s.principal.map(u -> div(div(span(u._2.name),
                                a("#", "Logout", on("click", ctx -> {
                                    principals.logout(ctx.sessionId().deviceId);
                                    pubSub.publish(ctx.sessionId().deviceId, "logout");
                                    us.accept(s.withoutPrincipal());
                                }))),
                        new MenuPanel().render(new MenuPanel.State(resources.resources().stream().map(r -> new Tuple2<>(r.name, r.title)).collect(Collectors.toList()))),

                        div(of(resourceView(s, us))))).orElse(div(loginView(s, us)))
                ));
    }

    private DocumentPartDefinition loginView(ViewState.Success s, UseState<ViewState> us) {
        return new LoginForm().render(new LoginForm.State(),
                lfs -> auth.authenticate(lfs.userName, lfs.password)
                        .thenAccept(po -> po.ifPresentOrElse(p -> lfs.deviceId.ifPresent(id -> {
                                    principals.login(id, p);
                                    us.accept(s.withPrincipal(new Tuple2<>(lfs.deviceId.get(), p)));
                                }),
                                () -> us.accept(s.withoutPrincipal()))));
    }

    private Stream<DocumentPartDefinition> resourceView(ViewState.Success s, UseState<ViewState> us) {
        return s.resourceState.flatMap(rs -> resources.resource(rs.name).map(p -> renderResourceView(s, p, rs, us))).stream();
    }

    private static DocumentPartDefinition renderResourceView(ViewState.Success s,
                                                             ResourceView<?> resourceView,
                                                             ResourceView.State<?> resourceState,
                                                             UseState<ViewState> appUseState) {
        final UseState<ResourceView.State<?>> resourceUseState = readWrite(() -> resourceState,
                v -> appUseState.accept(s.withResource(Optional.of(v))));
        return renderResourceViewUnchecked(resourceView, resourceUseState);
    }

    private static DocumentPartDefinition renderResourceViewUnchecked(ResourceView resourceView,
                                                                      UseState<ResourceView.State<?>> resourceUseState) {
        @SuppressWarnings("unchecked")
        final DocumentPartDefinition view = resourceView.render(resourceUseState);
        return view;
    }
}
