package com.mikuac.shiro.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * <p>NetUtils class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Slf4j
public class ReqUtils {

    private ReqUtils() {
    }

    public static String asyncGet(String url, int timeout) {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeout))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        CompletableFuture<String> result = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .exceptionally(e -> {
                    log.error("Request to {} failed: {}", url, e.getMessage());
                    return null;
                });

        try {
            return result.join();
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                log.warn("Request to {} interrupted", url);
            } else {
                log.error("Request to {} failed with exception: {}", url, cause.getMessage(), cause);
            }
        }

        return null;
    }

}
