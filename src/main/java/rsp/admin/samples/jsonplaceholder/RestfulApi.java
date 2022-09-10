package rsp.admin.samples.jsonplaceholder;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class RestfulApi {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private final String baseUrl;

    public RestfulApi(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public CompletableFuture<String> get(String path) {
            final HttpRequest request = HttpRequest.newBuilder(uri(path)).GET()
                                                   .timeout(REQUEST_TIMEOUT)
                                                   .build();
            return HttpClient.newHttpClient()
                             .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                             .thenApply(HttpResponse::body);
    }

    public CompletableFuture<String> post(String path, String body) {
        final HttpRequest request = HttpRequest.newBuilder(uri(path))
                                               .POST(HttpRequest.BodyPublishers.ofString(body))
                                               .timeout(REQUEST_TIMEOUT)
                                               .build();
        return HttpClient.newHttpClient()
                         .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                         .thenApply(HttpResponse::body);
    }

    public CompletableFuture<String> put(String path, String body) {
        final HttpRequest request = HttpRequest.newBuilder(uri(path))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .timeout(REQUEST_TIMEOUT)
                .build();
        return HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }

    public CompletableFuture<String> delete(String path) {
        final HttpRequest request = HttpRequest.newBuilder(uri(path))
                                               .DELETE()
                                               .timeout(REQUEST_TIMEOUT)
                                               .build();
        return HttpClient.newHttpClient()
                         .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                         .thenApply(HttpResponse::body);
    }

    private URI uri(String path) {
        try {
            return new URI(baseUrl + path);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
