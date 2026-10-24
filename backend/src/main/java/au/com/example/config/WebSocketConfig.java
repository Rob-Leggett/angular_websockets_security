package au.com.example.config;

import au.com.example.security.WebSocketTokenInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

/**
 * WebSocket Configuration - Modern replacement for WebSocketConfig.
 * 
 * Configures:
 * - STOMP over native WebSocket endpoint at /stomp
 * - Simple message broker for /api destinations
 * - Token-based authentication interceptor followed by message authorization
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketTokenInterceptor tokenInterceptor;
    private final AuthorizationManager<Message<?>> messageAuthorizationManager;
    private final ApplicationContext applicationContext;

    public WebSocketConfig(WebSocketTokenInterceptor tokenInterceptor,
                           AuthorizationManager<Message<?>> messageAuthorizationManager,
                           ApplicationContext applicationContext) {
        this.tokenInterceptor = tokenInterceptor;
        this.messageAuthorizationManager = messageAuthorizationManager;
        this.applicationContext = applicationContext;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker for subscription destinations
        config.enableSimpleBroker("/topic", "/queue");
        // Prefix for messages FROM client TO server
        config.setApplicationDestinationPrefixes("/app");
        // Prefix for user-specific destinations
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Native WebSocket STOMP endpoint (all supported browsers have WebSocket; no SockJS fallback needed)
        registry.addEndpoint("/stomp")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Order matters: authenticate the CONNECT frame from X-AUTH-TOKEN, propagate the session user
        // into the SecurityContext, then authorize the message (see WebSocketSecurityConfig).
        AuthorizationChannelInterceptor authorization = new AuthorizationChannelInterceptor(messageAuthorizationManager);
        authorization.setAuthorizationEventPublisher(new SpringAuthorizationEventPublisher(applicationContext));
        registration.interceptors(tokenInterceptor, new SecurityContextChannelInterceptor(), authorization);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }
}
