package au.ellie.hyui.utils;

import au.ellie.hyui.HyUIPlugin;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class PngDownloadUtils {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final long CACHE_TTL_MS = 15_000L;
    private static final java.util.Map<String, CacheEntry> CACHE = new java.util.HashMap<>();

    private PngDownloadUtils() {}

    public static byte[] downloadPng(String url) throws IOException, InterruptedException {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL cannot be null or blank.");
        }
        String normalizedUrl = url.trim();
        long now = System.currentTimeMillis();
        CacheEntry cached = getCache(normalizedUrl, now);
        if (cached != null) {
            HyUIPlugin.getLog().logInfo("PNG cache hit: " + normalizedUrl);
            return cached.bytes();
        }
        HyUIPlugin.getLog().logInfo("Downloading PNG: " + normalizedUrl);
        HttpRequest request = HttpRequest.newBuilder(URI.create(normalizedUrl))
                .GET()
                .header("Accept", "image/png")
                .build();
        HttpResponse<byte[]> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() != 200) {
            throw new IOException("Failed to download PNG. HTTP status: " + response.statusCode());
        }
        putCache(normalizedUrl, response.body(), now);
        HyUIPlugin.getLog().logInfo("Downloaded PNG bytes: " + response.body().length);
        return response.body();
    }

    private static CacheEntry getCache(String url, long now) {
        synchronized (CACHE) {
            CacheEntry entry = CACHE.get(url);
            if (entry != null && now - entry.createdAtMs() <= CACHE_TTL_MS) {
                return entry;
            }
            CACHE.remove(url);
            return null;
        }
    }

    private static void putCache(String url, byte[] bytes, long now) {
        synchronized (CACHE) {
            CACHE.put(url, new CacheEntry(bytes, now));
        }
    }

    private record CacheEntry(byte[] bytes, long createdAtMs) {}
}
