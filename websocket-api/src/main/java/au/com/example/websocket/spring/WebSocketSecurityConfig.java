package au.com.example.websocket.spring;

import au.com.example.security.spring.security.config.AbstractWebSocketSecurityConfig;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;

public class WebSocketSecurityConfig extends AbstractWebSocketSecurityConfig {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpTypeMatchers(
                        SimpMessageType.CONNECT,
                        SimpMessageType.MESSAGE,
                        SimpMessageType.SUBSCRIBE).authenticated()
                .simpTypeMatchers(
                        SimpMessageType.UNSUBSCRIBE,
                        SimpMessageType.DISCONNECT).permitAll()
                //.nullDestMatcher().authenticated()
                .anyMessage().denyAll();
    }
}
