package au.com.example.websocket.spring;

import au.com.example.security.spring.security.config.AbstractWebSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class WebSecurityConfig extends AbstractWebSecurityConfig {

    // ========= Overrides ===========

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .httpBasic()
                .authenticationEntryPoint(getUnauthorisedEntryPoint())
                .and()
            .authorizeRequests()
                .antMatchers("/stomp/**").permitAll()
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .addFilterBefore(getStatelessAuthenticationFilter(), BasicAuthenticationFilter.class)
            .addFilterAfter(getStatelessTokenAuthenticationFilter(), BasicAuthenticationFilter.class);
    }

    // =========== Beans ==============

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
