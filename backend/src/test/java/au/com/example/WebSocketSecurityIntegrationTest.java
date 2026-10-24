package au.com.example;

import au.com.example.security.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end check of the secured STOMP endpoint over a real WebSocket connection.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketSecurityIntegrationTest {

    @LocalServerPort
    private int port;

    private final WebSocketStompClient stompClient = createStompClient();

    private static WebSocketStompClient createStompClient() {
        WebSocketStompClient client = new WebSocketStompClient(new StandardWebSocketClient());
        client.setMessageConverter(new JacksonJsonMessageConverter());
        return client;
    }

    @AfterEach
    void tearDown() {
        stompClient.stop();
    }

    @Test
    void authenticatedClientReceivesNotificationsOnSubscribe() throws Exception {
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add(JwtTokenProvider.TOKEN_HEADER, login());

        StompSession session = connect(connectHeaders).get(10, TimeUnit.SECONDS);

        CompletableFuture<List<?>> payload = new CompletableFuture<>();
        session.subscribe("/app/user/notifications", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return List.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object body) {
                payload.complete((List<?>) body);
            }
        });

        String body = String.valueOf(payload.get(10, TimeUnit.SECONDS));
        assertTrue(body.contains("Welcome to the application!"), body);
        session.disconnect();
    }

    @Test
    void connectWithoutTokenIsRejected() {
        assertThrows(ExecutionException.class, () -> connect(new StompHeaders()).get(10, TimeUnit.SECONDS));
    }

    @Test
    void connectWithInvalidTokenIsRejected() {
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add(JwtTokenProvider.TOKEN_HEADER, "not-a-valid-token");

        assertThrows(ExecutionException.class, () -> connect(connectHeaders).get(10, TimeUnit.SECONDS));
    }

    private CompletableFuture<StompSession> connect(StompHeaders connectHeaders) {
        CompletableFuture<StompSession> result = new CompletableFuture<>();
        stompClient.connectAsync(URI.create("ws://localhost:" + port + "/stomp"), new WebSocketHttpHeaders(),
                connectHeaders, new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders headers) {
                        result.complete(session);
                    }

                    @Override
                    public void handleException(StompSession session, org.springframework.messaging.simp.stomp.StompCommand command,
                                                StompHeaders headers, byte[] payload, Throwable exception) {
                        result.completeExceptionally(exception);
                    }

                    @Override
                    public void handleTransportError(StompSession session, Throwable exception) {
                        result.completeExceptionally(exception);
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        // ERROR frame (e.g. access denied on CONNECT)
                        result.completeExceptionally(new IllegalStateException("STOMP error: " + headers.getFirst("message")));
                    }
                });
        return result;
    }

    private String login() throws Exception {
        String basic = "Basic " + Base64.getEncoder().encodeToString("user@example.com:password".getBytes());
        HttpResponse<String> response = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/authentication/login"))
                        .header("Authorization", basic)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        return response.headers().firstValue(JwtTokenProvider.TOKEN_HEADER).orElseThrow();
    }
}
