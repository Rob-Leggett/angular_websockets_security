package au.com.example.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * WebSocket Token Interceptor - Modern replacement for TokenSecurityChannelInterceptor.
 * 
 * CRITICAL SECURITY COMPONENT
 * 
 * This interceptor validates the X-AUTH-TOKEN passed in STOMP headers during
 * WebSocket connections. It ensures that:
 * 
 * 1. Token is extracted from native headers on CONNECT
 * 2. Token is validated using JWT
 * 3. SecurityContext is set for the WebSocket session
 * 4. Authenticated principal is available for message handling
 * 
 * This preserves the exact security model from the original application.
 */
@Component
public class WebSocketTokenInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketTokenInterceptor.class);

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    public WebSocketTokenInterceptor(JwtTokenProvider tokenProvider, UserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Extract token from native headers (same as original implementation)
            String token = accessor.getFirstNativeHeader(JwtTokenProvider.TOKEN_HEADER);

            if (StringUtils.hasText(token)) {
                logger.debug("WebSocket CONNECT: Validating token");

                if (tokenProvider.validateToken(token)) {
                    String username = tokenProvider.getUsernameFromToken(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    // Set the user for this WebSocket session
                    accessor.setUser(authentication);
                    
                    // Also set in SecurityContext for immediate use
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.debug("WebSocket authenticated user: {}", username);
                } else {
                    logger.warn("WebSocket CONNECT: Invalid token");
                }
            } else {
                logger.warn("WebSocket CONNECT: No token provided");
            }
        }

        return message;
    }
}
