package rsp.admin.crud.components.main;

import rsp.admin.auth.Principal;
import rsp.util.data.Tuple2;

import java.util.Optional;

public interface ViewState {

    NotFound NOT_FOUND = new NotFound();

    class Error implements ViewState {
    }

    class NotFound implements ViewState {
    }

    class Success implements ViewState {
        public final Optional<Tuple2<String, Principal>> principal;
        public final Optional<ResourceView.State<?>> resourceState;

        public Success(Optional<Tuple2<String, Principal>> principal, Optional<ResourceView.State<?>> resourceState) {
            this.principal = principal;
            this.resourceState = resourceState;
        }

        public Success withResource(Optional<ResourceView.State<?>> currentResource) {
            return new Success(this.principal, currentResource);
        }

        public Success withPrincipal(Tuple2<String, Principal> principal) {
            return new Success(Optional.of(principal), this.resourceState);
        }

        public Success withoutPrincipal() {
            return new Success(Optional.empty(), this.resourceState);
        }
    }
}
