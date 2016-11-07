package au.com.example.security.service.authentication;

import org.springframework.security.core.Authentication;

public interface TokenAuthenticationService {

    String createToken(Authentication authentication);

    Authentication getAuthentication(String token);
}
