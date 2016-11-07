package au.com.example.security.spring.security.config;

import au.com.example.security.spring.security.interceptor.TokenSecurityChannelInterceptor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.messaging.access.expression.MessageExpressionVoter;
import org.springframework.security.messaging.access.intercept.ChannelSecurityInterceptor;
import org.springframework.security.messaging.access.intercept.MessageSecurityMetadataSource;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.messaging.web.csrf.CsrfChannelInterceptor;
import org.springframework.security.messaging.web.socket.server.CsrfTokenHandshakeInterceptor;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;
import org.springframework.web.socket.sockjs.SockJsService;
import org.springframework.web.socket.sockjs.support.SockJsHttpRequestHandler;
import org.springframework.web.socket.sockjs.transport.TransportHandlingSockJsService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AbstractSecurityWebSocketMessageBrokerConfig extends AbstractWebSocketMessageBrokerConfigurer implements SmartInitializingSingleton {
    private final AbstractSecurityWebSocketMessageBrokerConfig.WebSocketMessageSecurityMetadataSourceRegistry inboundRegistry = new AbstractSecurityWebSocketMessageBrokerConfig.WebSocketMessageSecurityMetadataSourceRegistry();

    @Autowired
    private ApplicationContext context;

    @Autowired
    private TokenSecurityChannelInterceptor tokenSecurityChannelInterceptor;

    public AbstractSecurityWebSocketMessageBrokerConfig() {
    }

    public void registerStompEndpoints(StompEndpointRegistry registry) {
    }

    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    public void configureClientInboundChannel(ChannelRegistration registration) {
        ChannelSecurityInterceptor inboundChannelSecurity = this.inboundChannelSecurity();

        registration.setInterceptors(new ChannelInterceptor[]{this.securityContextChannelInterceptor()});

        if (!this.sameOriginDisabled()) {
            registration.setInterceptors(new ChannelInterceptor[]{this.csrfChannelInterceptor()});
        }

        if (this.inboundRegistry.containsMapping()) {
            registration.setInterceptors(new ChannelInterceptor[]{inboundChannelSecurity});
        }

        this.customizeClientInboundChannel(registration);
    }

    protected boolean sameOriginDisabled() {
        return true;
    }

    protected void customizeClientInboundChannel(ChannelRegistration registration) {
    }

    @Bean
    public ChannelInterceptorAdapter securityContextChannelInterceptor() {
        return tokenSecurityChannelInterceptor;
    }

    @Bean
    public CsrfChannelInterceptor csrfChannelInterceptor() {
        return new CsrfChannelInterceptor();
    }

    @Bean
    public ChannelSecurityInterceptor inboundChannelSecurity() {
        ChannelSecurityInterceptor channelSecurityInterceptor = new ChannelSecurityInterceptor(this.inboundMessageSecurityMetadataSource());
        ArrayList voters = new ArrayList();
        voters.add(new MessageExpressionVoter());
        AffirmativeBased manager = new AffirmativeBased(voters);
        channelSecurityInterceptor.setAccessDecisionManager(manager);
        return channelSecurityInterceptor;
    }

    @Bean
    public MessageSecurityMetadataSource inboundMessageSecurityMetadataSource() {
        this.configureInbound(this.inboundRegistry);
        return this.inboundRegistry.createMetadataSource();
    }

    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
    }

    public void afterSingletonsInstantiated() {
        if (!this.sameOriginDisabled()) {
            String beanName = "stompWebSocketHandlerMapping";
            SimpleUrlHandlerMapping mapping = this.context.getBean(beanName, SimpleUrlHandlerMapping.class);
            Map mappings = mapping.getHandlerMap();
            Iterator i$ = mappings.values().iterator();

            while (i$.hasNext()) {
                Object object = i$.next();
                if (object instanceof SockJsHttpRequestHandler) {
                    SockJsHttpRequestHandler handler = (SockJsHttpRequestHandler) object;
                    SockJsService handshakeInterceptors = handler.getSockJsService();
                    if (!(handshakeInterceptors instanceof TransportHandlingSockJsService)) {
                        throw new IllegalStateException("sockJsService must be instance of TransportHandlingSockJsService got " + handshakeInterceptors);
                    }

                    TransportHandlingSockJsService interceptorsToSet = (TransportHandlingSockJsService) handshakeInterceptors;
                    List handshakeInterceptors1 = interceptorsToSet.getHandshakeInterceptors();
                    ArrayList interceptorsToSet1 = new ArrayList(handshakeInterceptors1.size() + 1);
                    interceptorsToSet1.add(new CsrfTokenHandshakeInterceptor());
                    interceptorsToSet1.addAll(handshakeInterceptors1);
                    interceptorsToSet.setHandshakeInterceptors(interceptorsToSet1);
                } else {
                    if (!(object instanceof WebSocketHttpRequestHandler)) {
                        throw new IllegalStateException("Bean " + beanName + " is expected to contain mappings to either a SockJsHttpRequestHandler or a WebSocketHttpRequestHandler but got " + object);
                    }

                    WebSocketHttpRequestHandler handler1 = (WebSocketHttpRequestHandler) object;
                    List handshakeInterceptors2 = handler1.getHandshakeInterceptors();
                    ArrayList interceptorsToSet2 = new ArrayList(handshakeInterceptors2.size() + 1);
                    interceptorsToSet2.add(new CsrfTokenHandshakeInterceptor());
                    interceptorsToSet2.addAll(handshakeInterceptors2);
                    handler1.setHandshakeInterceptors(interceptorsToSet2);
                }
            }

        }
    }

    private class WebSocketMessageSecurityMetadataSourceRegistry extends MessageSecurityMetadataSourceRegistry {
        private WebSocketMessageSecurityMetadataSourceRegistry() {
        }

        public MessageSecurityMetadataSource createMetadataSource() {
            return super.createMetadataSource();
        }

        protected boolean containsMapping() {
            return super.containsMapping();
        }
    }
}