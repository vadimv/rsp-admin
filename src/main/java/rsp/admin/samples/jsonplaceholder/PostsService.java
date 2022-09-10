package rsp.admin.samples.jsonplaceholder;

import com.jsoniter.JsonIterator;
import rsp.admin.data.entity.KeyedEntity;
import rsp.admin.data.provider.EntityService;
import rsp.admin.data.provider.GetListQuery;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PostsService implements EntityService<String, Post> {
    private final RestfulApi restfulApi = new RestfulApi(JsonPlaceholderAdmin.JSON_PLACEHOLDER_BASE_URL);
    @Override
    public CompletableFuture<List<KeyedEntity<String, Post>>> getList(GetListQuery<String> query) {
        return restfulApi.get("posts")
                .thenApply(s -> JsonIterator.deserialize(s, Post[].class))
                .thenApply(posts -> Arrays.stream(posts).map(post -> new KeyedEntity<>(Integer.toString(post.id), post)).toList());
    }

    @Override
    public CompletableFuture<KeyedEntity<String, Post>> create(Post entity) {
        return null;
    }

    @Override
    public CompletableFuture<KeyedEntity<String, Post>> delete(String key) {
        return null;
    }



    @Override
    public CompletableFuture<KeyedEntity<String, Post>> getOne(String key) {
        return null;
    }

    @Override
    public CompletableFuture<KeyedEntity<String, Post>> update(KeyedEntity<String, Post> updatedKeyedEntity) {
        return null;
    }
}
