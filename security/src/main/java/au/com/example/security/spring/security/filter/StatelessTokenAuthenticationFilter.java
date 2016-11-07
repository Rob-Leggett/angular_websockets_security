package au.com.example.security.spring.security.filter;

import au.com.example.security.constant.Constants;
import au.com.example.security.service.authentication.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(value = Constants.SECURITY_STATELESS_TOKEN_AUTH_FILTER)
public class StatelessTokenAuthenticationFilter extends GenericFilterBean {

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            String token = tokenAuthenticationService.createToken(SecurityContextHolder.getContext().getAuthentication());

            ((HttpServletResponse) response).setHeader(Constants.HEADER_X_AUTH_TOKEN, token);
        }

        chain.doFilter(request, response); // continue always
    }
}
