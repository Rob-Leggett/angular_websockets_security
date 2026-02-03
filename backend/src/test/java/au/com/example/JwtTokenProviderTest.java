package au.com.example;

import au.com.example.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Test
    void testCreateAndValidateToken() {
        UserDetails userDetails = new User(
                "test@example.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String token = tokenProvider.createToken(auth);
        
        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("test@example.com", tokenProvider.getUsernameFromToken(token));
    }

    @Test
    void testInvalidToken() {
        assertFalse(tokenProvider.validateToken("invalid.token.here"));
    }

    @Test
    void testMalformedToken() {
        assertFalse(tokenProvider.validateToken("not-a-jwt"));
    }
}
