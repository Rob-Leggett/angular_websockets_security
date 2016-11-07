package au.com.example.security.spring.security.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {"au.com.example.security.service", "au.com.example.security.spring.security"})
@PropertySource("classpath:properties/security.properties")
public abstract class AbstractWebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfig {
}
