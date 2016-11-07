package au.com.example.websocket.config;

import au.com.example.websocket.spring.AppConfig;
import au.com.example.websocket.spring.ServiceConfig;
import au.com.example.websocket.spring.WebSocketSecurityConfig;
import au.com.example.websocket.spring.PersistenceConfig;
import au.com.example.websocket.spring.PropertyConfig;
import au.com.example.websocket.spring.WebSecurityConfig;
import au.com.example.websocket.spring.WebSocketConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.EnumSet;

public class AppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        servletContext.addListener(new ContextLoaderListener(getContext()));

        // spring security
        FilterRegistration securityFilter = servletContext.addFilter("springSecurityFilterChain", new DelegatingFilterProxy("springSecurityFilterChain"));
        securityFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), true, "/*");

        // character encoding
        FilterRegistration encodingFilter = servletContext.addFilter("characterEncodingFilter", getEncodingFilter());
        encodingFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), true, "/*");

        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(getDispatchContext()));
        servlet.addMapping("/");
        servlet.setAsyncSupported(true);
        servlet.setLoadOnStartup(1);
    }

    // ====== Helpers ========

    private AnnotationConfigWebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(WebSecurityConfig.class);
        context.register(PropertyConfig.class);
        context.register(ServiceConfig.class);
        context.register(PersistenceConfig.class);

        return context;
    }

    private AnnotationConfigWebApplicationContext getDispatchContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);
        context.register(WebSecurityConfig.class);
        context.register(WebSocketSecurityConfig.class);
        context.register(WebSocketConfig.class);
        context.register(PropertyConfig.class);

        return context;
    }

    private CharacterEncodingFilter getEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);

        return filter;
    }
}


