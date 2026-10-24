package au.com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

/**
 * WebSocket Security Configuration.
 *
 * CRITICAL SECURITY COMPONENT
 *
 * Defines message-level authorization rules for STOMP:
 * - CONNECT, MESSAGE, SUBSCRIBE require authentication
 * - DISCONNECT, UNSUBSCRIBE are permitted
 * - All other messages are denied
 *
 * The rules are enforced by an AuthorizationChannelInterceptor registered in {@link WebSocketConfig}
 * (after the token interceptor). {@code @EnableWebSocketSecurity} is deliberately not used because it
 * always enforces a CSRF token on CONNECT, which does not apply to header-token authentication.
 */
@Configuration
public class WebSocketSecurityConfig {

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager() {
        return MessageMatcherDelegatingAuthorizationManager.builder()
                .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE)
                .authenticated()
                .simpTypeMatchers(SimpMessageType.UNSUBSCRIBE, SimpMessageType.DISCONNECT)
                .permitAll()
                .anyMessage().denyAll()
                .build();
    }
}
