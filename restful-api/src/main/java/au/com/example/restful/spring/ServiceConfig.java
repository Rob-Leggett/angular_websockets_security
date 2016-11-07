package au.com.example.restful.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "au.com.example.restful.service" })
public class ServiceConfig {

}
