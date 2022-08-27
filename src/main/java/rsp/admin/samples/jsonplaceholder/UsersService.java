package rsp.admin.samples.jsonplaceholder;

import com.jsoniter.JsonIterator;
import rsp.admin.data.entity.KeyedEntity;
import rsp.admin.data.provider.EntityService;
import rsp.admin.data.provider.GetListQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UsersService implements EntityService<String, User> {
    private final JsonPlaceholderHttp http = new JsonPlaceholderHttp();

    @Override
    public CompletableFuture<Optional<KeyedEntity<String, User>>> create(User entity) {
        return null;
    }

    @Override
    public CompletableFuture<Optional<KeyedEntity<String, User>>> delete(String key) {
        return null;
    }

    @Override
    public CompletableFuture<List<KeyedEntity<String, User>>> getList(GetListQuery<String> query) {
        return http.get("users")
                .thenApply(s -> JsonIterator.deserialize(s, User[].class))
                .thenApply(users -> Arrays.stream(users).map(user -> new KeyedEntity<>(Integer.toString(user.id), user)).toList());
    }

    @Override
    public CompletableFuture<Optional<KeyedEntity<String, User>>> getOne(String key) {
        return http.get("users/" + key)
                .thenApply(s -> JsonIterator.deserialize(s, User.class))
                .thenApply(user -> Optional.of(new KeyedEntity<>(Integer.toString(user.id), user)));

    }

    @Override
    public CompletableFuture<Optional<KeyedEntity<String, User>>> update(KeyedEntity<String, User> updatedKeyedEntity) {
        return null;
    }
}
