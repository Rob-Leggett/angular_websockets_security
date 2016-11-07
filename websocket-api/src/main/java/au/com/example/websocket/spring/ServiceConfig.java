package au.com.example.websocket.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "au.com.example.websocket.service" })
public class ServiceConfig {

}
