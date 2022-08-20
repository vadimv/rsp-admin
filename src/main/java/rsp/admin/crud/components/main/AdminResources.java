package rsp.admin.crud.components.main;

import rsp.admin.auth.Principal;
import rsp.util.data.Tuple2;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class AdminResources {

    private final List<ResourceView<?>> resources;

    public AdminResources(List<ResourceView<?>> resources) {
        this.resources = resources;
    }

    public List<ResourceView<?>> resources() {
        return resources;
    }

    public Optional<ResourceView<?>> resource(String name) {
        return resources.stream().filter(r -> name.equals(r.name)).findFirst();
    }

    public CompletableFuture<ViewState> resourceList(String resourceName,
                                                     Optional<Tuple2<String, Principal>> principal) {
        return  resource(resourceName).map(resource -> listState(principal, resource))
                .orElse(CompletableFuture.completedFuture(ViewState.NOT_FOUND));
    }

    public CompletableFuture<ViewState> resourceDetails(String resourceName,
                                                        String key,
                                                        Optional<Tuple2<String, Principal>> principal) {
        return  resource(resourceName).map(resource -> listStateWKey(principal, resource, key))
                                      .orElse(CompletableFuture.completedFuture(ViewState.NOT_FOUND));
    }

    private CompletableFuture<ViewState> listState(Optional<Tuple2<String,
                                                   Principal>> principal,
                                                   ResourceView<?> resource) {
        return resource.initialListState().thenApply(resourceState -> new ViewState.Success(principal,
                                                                                            Optional.of(resourceState)));
    }


    private CompletableFuture<ViewState> listStateWKey(Optional<Tuple2<String, Principal>> principal,
                                                       ResourceView<?> resource,
                                                       String key) {
        return resource.initialListStateWithEdit(key).thenApply(resourceState -> new ViewState.Success(principal,
                                                                                                       Optional.of(resourceState)));
    }
}
