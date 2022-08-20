package rsp.admin.crud.components.main;

import rsp.admin.auth.Principal;
import rsp.routing.Route;
import rsp.server.HttpRequest;
import rsp.server.Path;
import rsp.util.data.Tuple2;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static rsp.routing.RoutingDsl.*;
import static rsp.routing.RoutingDsl.any;

public final class AdminRouting {

    private final AdminResources resources;
    private final Principals principals;

    public AdminRouting(AdminResources resources, Principals principals) {
        this.resources = resources;
        this.principals = principals;
    }

    public Route<HttpRequest, ViewState> routes() {
        return concat(get(req -> paths(principals.principal(req.deviceId()))),
                any(ViewState.NOT_FOUND));
    }

    public Route<Path, ViewState> paths(Optional<Tuple2<String, Principal>> principal) {
        return concat(path("",  CompletableFuture.completedFuture(new ViewState.Success(principal, Optional.empty()))),
                path("/login", CompletableFuture.completedFuture(new ViewState.Success(Optional.empty(), Optional.empty()))),
                path("/:resourceName", resourceName -> resources.resourceList(resourceName, principal)),
                path( "/:resourceName/:key", (resourceName, key) -> resources.resourceDetails(resourceName, key, principal)));
    }

    public static Path stateToPath(ViewState appState, Path p) {
        if (appState instanceof ViewState.Success) {
            final var s = (ViewState.Success) appState;
            if (s.principal.isPresent()) {
                return s.resourceState.map(state -> state.details.map(detailsViewState -> Path.of(state.name
                                + "/" + detailsViewState.currentKey.orElse("create")))
                        .orElseGet(() -> Path.of(state.name))).orElse(Path.EMPTY_ABSOLUTE);
            } else {
                return Path.of("login");
            }
        } else if (appState instanceof ViewState.NotFound){
            Path.of("notFound");
        }
        assert appState instanceof ViewState.Error;
        return Path.of("error");
    }
}
