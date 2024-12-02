package c.block.api;

import c.block.model.TsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

public class IitisLabsAPIService {

    public static TsResponse ts(String hex) throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI("http://itislabs.ru/ts?digest=" + hex))
                .build();
        String responseBody = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).get();
        /*if (responseBody.contains("404") | responseBody.contains("500")) {
            // retry
            responseBody = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).get();
            while (responseBody.contains("404")) {
                Thread.sleep(1000);
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).get();
            }
        } */
        ObjectMapper mapper = new ObjectMapper();
        return mapper.reader().readValue(responseBody, TsResponse.class);
    }

    public static String publicKey() throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/png,image/svg+xml,*/*;")
                .header("Referer", "http://itislabs.ru/ts/public")
                .uri(new URI("http://itislabs.ru/ts/public"))
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).get();
    }

    public static String publicKey64() throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/png,image/svg+xml,*/*;")
                .header("Referer", "http://itislabs.ru/ts/public")
                .uri(new URI("http://itislabs.ru/ts/public64"))
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).get();
    }

}
