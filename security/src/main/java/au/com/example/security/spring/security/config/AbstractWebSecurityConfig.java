package au.com.example.security.spring.security.config;

import au.com.example.security.persistence.provider.DaoUserDetailsAuthenticationProvider;
import au.com.example.security.service.user.UserDetailService;
import au.com.example.security.spring.security.entry.UnauthorisedEntryPoint;
import au.com.example.security.spring.security.filter.StatelessAuthenticationFilter;
import au.com.example.security.spring.security.filter.StatelessTokenAuthenticationFilter;
import au.com.example.security.spring.security.manager.AuthenticationProviderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@ComponentScan(basePackages = {"au.com.example.security.service", "au.com.example.security.spring.security"})
@PropertySource("classpath:properties/security.properties")
public abstract class AbstractWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UnauthorisedEntryPoint unauthorisedEntryPoint;

    @Autowired
    private StatelessAuthenticationFilter statelessAuthenticationFilter;

    @Autowired
    private StatelessTokenAuthenticationFilter statelessTokenAuthenticationFilter;

    @Autowired
    private UserDetailService userDetailService;

    private AuthenticationManager authenticationManager;

    // ======= Overrides =======

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        if (authenticationManager == null) {
            authenticationManager = new AuthenticationProviderManager(getAuthenticationProviders());
        }

        return authenticationManager;
    }

    // ======= Getters =======

    protected List<AuthenticationProvider> getAuthenticationProviders() {
        List<AuthenticationProvider> providers = new ArrayList<>();

        DaoUserDetailsAuthenticationProvider daoUserDetailsAuthenticationProvider = new DaoUserDetailsAuthenticationProvider();
        daoUserDetailsAuthenticationProvider.setUserDetailsService(getUserDetailService());
        daoUserDetailsAuthenticationProvider.setPasswordEncoder(getPasswordEncoder());

        providers.add(daoUserDetailsAuthenticationProvider);

        return providers;
    }

    protected UnauthorisedEntryPoint getUnauthorisedEntryPoint() {
        return unauthorisedEntryPoint;
    }

    protected StatelessAuthenticationFilter getStatelessAuthenticationFilter() {
        return statelessAuthenticationFilter;
    }

    protected StatelessTokenAuthenticationFilter getStatelessTokenAuthenticationFilter() {
        return statelessTokenAuthenticationFilter;
    }

    protected UserDetailService getUserDetailService() {
        return userDetailService;
    }

    // ======= Beans =======

    @Bean(name = "passwordEncoder")
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4444", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Location", "X-AUTH-TOKEN", "Access-Control-Expose-Headers", "Cache-Control", "Content-Language", "Content-Type", "Expires", "Last-Modified", "Pragma"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
