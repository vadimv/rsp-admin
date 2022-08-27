package rsp.admin.samples.jsonplaceholder;

import com.jsoniter.JsonIterator;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class JsonPlaceholderHttpTest {
    @Test
    public void test() throws ExecutionException, InterruptedException {
        final JsonPlaceholderHttp http = new JsonPlaceholderHttp();
        System.out.println(http.get("posts/1").get());
        var users = http.get("posts/1").thenApply(s -> JsonIterator.deserialize(s, Post.class)).get();
        Thread.sleep( 1000);
    }
}
