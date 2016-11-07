package au.com.example.security.spring.security.filter;

import au.com.example.security.constant.Constants;
import au.com.example.security.service.authentication.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component(value = Constants.SECURITY_STATELESS_AUTH_FILTER)
public class StatelessAuthenticationFilter extends GenericFilterBean {

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = ((HttpServletRequest) request).getHeader(Constants.HEADER_X_AUTH_TOKEN);

        SecurityContextHolder.getContext().setAuthentication(tokenAuthenticationService.getAuthentication(token));

        chain.doFilter(request, response); // continue always
    }
}
