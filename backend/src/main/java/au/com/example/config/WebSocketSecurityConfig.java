package au.com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * WebSocket Security Configuration - Modern replacement for WebSocketSecurityConfig.
 * 
 * CRITICAL SECURITY COMPONENT
 * 
 * Configures message-level security for STOMP:
 * - CONNECT, MESSAGE, SUBSCRIBE require authentication
 * - DISCONNECT, UNSUBSCRIBE are permitted
 * - All other messages are denied
 * 
 * This preserves the exact security rules from the original application.
 */
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
            // Require authentication for connecting and messaging
            .simpTypeMatchers(
                org.springframework.messaging.simp.SimpMessageType.CONNECT,
                org.springframework.messaging.simp.SimpMessageType.MESSAGE,
                org.springframework.messaging.simp.SimpMessageType.SUBSCRIBE
            ).authenticated()
            // Allow disconnect without auth
            .simpTypeMatchers(
                org.springframework.messaging.simp.SimpMessageType.UNSUBSCRIBE,
                org.springframework.messaging.simp.SimpMessageType.DISCONNECT
            ).permitAll()
            // Deny everything else
            .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        // Disable CSRF for WebSocket since we use token-based auth
        return true;
    }
}
