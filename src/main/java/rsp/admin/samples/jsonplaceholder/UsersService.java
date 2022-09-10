package rsp.admin.samples.jsonplaceholder;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import rsp.admin.data.entity.KeyedEntity;
import rsp.admin.data.provider.EntityService;
import rsp.admin.data.provider.GetListQuery;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UsersService implements EntityService<String, User> {
    private final RestfulApi restfulApi = new RestfulApi(JsonPlaceholderAdmin.JSON_PLACEHOLDER_BASE_URL);

    @Override
    public CompletableFuture<KeyedEntity<String, User>> create(User entity) {
        return restfulApi.post("users/", JsonStream.serialize(entity))
                .thenApply(s -> JsonIterator.deserialize(s, User.class))
                .thenApply(user -> new KeyedEntity<>(Integer.toString(user.id), user));
    }

    @Override
    public CompletableFuture<KeyedEntity<String, User>> update(KeyedEntity<String, User> updatedKeyedEntity) {
        return restfulApi.put("users/" + updatedKeyedEntity.key, JsonStream.serialize(updatedKeyedEntity.data))
                .thenApply(s -> JsonIterator.deserialize(s, User.class))
                .thenApply(user -> new KeyedEntity<>(Integer.toString(user.id), user));
    }

    @Override
    public CompletableFuture<KeyedEntity<String, User>> delete(String key) {
        return restfulApi.delete("users/" + key )
                .thenApply(s -> JsonIterator.deserialize(s, User.class))
                .thenApply(user -> new KeyedEntity<>(Integer.toString(user.id), user));
    }

    @Override
    public CompletableFuture<List<KeyedEntity<String, User>>> getList(GetListQuery<String> query) {
        return restfulApi.get("users")
                .thenApply(s -> JsonIterator.deserialize(s, User[].class))
                .thenApply(users -> Arrays.stream(users).map(user -> new KeyedEntity<>(Integer.toString(user.id), user)).toList());
    }

    @Override
    public CompletableFuture<KeyedEntity<String, User>> getOne(String key) {
        return restfulApi.get("users/" + key)
                .thenApply(s -> JsonIterator.deserialize(s, User.class))
                .thenApply(user -> new KeyedEntity<>(Integer.toString(user.id), user));

    }

}
