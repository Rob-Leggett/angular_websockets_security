package au.com.example.security.spring.security.manager;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;

import java.util.List;

public class AuthenticationProviderManager extends ProviderManager {

    public AuthenticationProviderManager(List<AuthenticationProvider> providers) {
        super(providers);
    }
}
