package au.com.example.websocket.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = { "au.com.example.security.persistence", "au.com.example.websocket.persistence" })
public class PersistenceConfig {

    @Bean
    public LocalEntityManagerFactoryBean entityManagerFactory(@Value("${persistenceUnitName}") String persistenceUnitName) {
        LocalEntityManagerFactoryBean em = new LocalEntityManagerFactoryBean();
        em.setPersistenceUnitName(persistenceUnitName);

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }
}
