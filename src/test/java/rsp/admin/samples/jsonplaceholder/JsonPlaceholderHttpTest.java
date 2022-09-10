package rsp.admin.samples.jsonplaceholder;

import com.jsoniter.JsonIterator;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class JsonPlaceholderHttpTest {
    @Test
    public void test() throws ExecutionException, InterruptedException {
        final RestfulApi http = new RestfulApi(JsonPlaceholderAdmin.JSON_PLACEHOLDER_BASE_URL);
        System.out.println(http.get("posts/1").get());
        var users = http.get("posts/1").thenApply(s -> JsonIterator.deserialize(s, Post.class)).get();
        Thread.sleep( 1000);
    }
}
