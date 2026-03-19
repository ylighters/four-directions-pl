package com.example.payment.infrastructure.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Component
public class ChannelHttpExecutor {

    private static final int MAX_RETRY = 2;
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);
    private static final int BULKHEAD_LIMIT_PER_CHANNEL = 300;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(CONNECT_TIMEOUT)
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    private final ObjectMapper objectMapper;
    private final Map<String, Semaphore> bulkheadMap = new ConcurrentHashMap<>();

    public ChannelHttpExecutor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String postJsonWithRetry(String channelCode,
                                    String url,
                                    Map<String, String> headers,
                                    Map<String, Object> payload) {
        Semaphore semaphore = bulkheadMap.computeIfAbsent(channelCode, k -> new Semaphore(BULKHEAD_LIMIT_PER_CHANNEL));
        if (!semaphore.tryAcquire()) {
            throw new ChannelInvokeException("Channel bulkhead rejected: " + channelCode);
        }
        try {
            String body = objectMapper.writeValueAsString(payload);
            Exception last = null;
            for (int i = 0; i <= MAX_RETRY; i++) {
                try {
                    HttpRequest.Builder builder = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .timeout(REQUEST_TIMEOUT)
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(body));
                    if (headers != null) {
                        headers.forEach(builder::header);
                    }
                    HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
                    int status = response.statusCode();
                    if (status >= 200 && status < 300) {
                        return response.body() == null ? "" : response.body();
                    }
                    if (status >= 500 && i < MAX_RETRY) {
                        sleepBackoff(i);
                        continue;
                    }
                    throw new ChannelInvokeException("Channel http status error: " + status + ", body=" + response.body());
                } catch (Exception ex) {
                    last = ex;
                    if (i < MAX_RETRY) {
                        sleepBackoff(i);
                        continue;
                    }
                }
            }
            throw new ChannelInvokeException("Channel request failed after retry", last);
        } catch (ChannelInvokeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ChannelInvokeException("Serialize request failed", ex);
        } finally {
            semaphore.release();
        }
    }

    private void sleepBackoff(int retryIndex) {
        try {
            Thread.sleep(120L * (retryIndex + 1));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
