package rsp.admin.auth;

import rsp.admin.auth.Principal;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Auth {
    public CompletableFuture<Optional<Principal>> authenticate(String userName, String password) {
        return "admin".equals(userName) && "admin".equals(password) ? principal(userName) : CompletableFuture.completedFuture(Optional.empty());
    }

    private CompletableFuture<Optional<Principal>> principal(String userName) {
        return CompletableFuture.completedFuture(Optional.of(new Principal(userName)));
    }
}
