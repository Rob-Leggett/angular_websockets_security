package au.com.example.security.spring.security.interceptor;

import au.com.example.security.constant.Constants;
import au.com.example.security.service.authentication.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

@Component(value = Constants.SECURITY_TOKEN_SECURITY_CHANNEL_INTERCEPTOR)
public class TokenSecurityChannelInterceptor extends ChannelInterceptorAdapter implements ExecutorChannelInterceptor {

    private static final ThreadLocal<Stack<SecurityContext>> ORIGINAL_CONTEXT = new ThreadLocal<>();

    private final SecurityContext EMPTY_CONTEXT;
    private final Authentication anonymous;

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    public TokenSecurityChannelInterceptor() {
        this.EMPTY_CONTEXT = SecurityContextHolder.createEmptyContext();
        this.anonymous = new AnonymousAuthenticationToken("key", "anonymous", AuthorityUtils.createAuthorityList(new String[]{"ROLE_ANONYMOUS"}));
    }

    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        this.setup(message);
        return message;
    }

    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        this.cleanup();
    }

    @Override
    public Message<?> beforeHandle(Message<?> message, MessageChannel channel, MessageHandler handler) {
        this.setup(message);
        return message;
    }

    @Override
    public void afterMessageHandled(Message<?> message, MessageChannel channel, MessageHandler handler, Exception ex) {
        this.cleanup();
    }

    private void setup(Message<?> message) {
        SecurityContext currentContext = SecurityContextHolder.getContext();
        Stack contextStack = (Stack) ORIGINAL_CONTEXT.get();
        if (contextStack == null) {
            contextStack = new Stack();
            ORIGINAL_CONTEXT.set(contextStack);
        }

        contextStack.push(currentContext);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(getAuthentication(message.getHeaders()));
        SecurityContextHolder.setContext(context);
    }

    private Authentication getAuthentication(MessageHeaders messageHeaders) {
        Authentication authentication = this.anonymous;

        Map<String, LinkedList> nativeHeaders = (Map<String, LinkedList>) messageHeaders.get("nativeHeaders");

        if (nativeHeaders != null) {
            LinkedList token = nativeHeaders.get(Constants.HEADER_X_AUTH_TOKEN);

            if(token != null) {
                Authentication tokenAuthentication = tokenAuthenticationService.getAuthentication(token.getFirst().toString());

                if (tokenAuthentication != null) {
                    authentication = tokenAuthentication;
                }
            }
        }

        return authentication;
    }

    private void cleanup() {
        Stack contextStack = (Stack) ORIGINAL_CONTEXT.get();
        if (contextStack != null && !contextStack.isEmpty()) {
            SecurityContext originalContext = (SecurityContext) contextStack.pop();

            try {
                if (this.EMPTY_CONTEXT.equals(originalContext)) {
                    SecurityContextHolder.clearContext();
                    ORIGINAL_CONTEXT.remove();
                } else {
                    SecurityContextHolder.setContext(originalContext);
                }
            } catch (Throwable var4) {
                SecurityContextHolder.clearContext();
            }

        } else {
            SecurityContextHolder.clearContext();
            ORIGINAL_CONTEXT.remove();
        }
    }
}
