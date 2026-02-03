package au.com.example.controller;

import au.com.example.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Map;

/**
 * Authentication Controller - Modern replacement for AuthenticationController.
 * 
 * Handles:
 * - Login with Basic Auth (decodes credentials, authenticates, returns token)
 * - Logout (client-side token removal)
 * 
 * Maintains the same API contract as the original for client compatibility.
 */
@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthenticationController(AuthenticationManager authenticationManager, 
                                   JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Login endpoint - accepts Basic Auth header, returns JWT in X-AUTH-TOKEN header.
     * Maintains compatibility with original AngularJS client.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletResponse response) {
        
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Missing or invalid Authorization header"));
        }

        try {
            // Decode Basic Auth credentials
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] parts = credentials.split(":", 2);
            
            if (parts.length != 2) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid credentials format"));
            }

            String email = parts[0];
            String password = parts[1];

            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String token = tokenProvider.createToken(authentication);

            // Return token in header (same as original)
            response.setHeader(JwtTokenProvider.TOKEN_HEADER, token);

            logger.info("User logged in: {}", email);

            return ResponseEntity.ok(Map.of("message", "Login successful"));

        } catch (Exception e) {
            logger.error("Authentication failed", e);
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Authentication failed"));
        }
    }

    /**
     * Logout endpoint - server-side logout is a no-op for stateless JWT.
     * Client should remove the token from storage.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }
}
