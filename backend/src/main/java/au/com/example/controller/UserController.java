package au.com.example.controller;

import au.com.example.model.User;
import au.com.example.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * User Controller - Modern replacement for UserController.
 * 
 * Provides current user information for authenticated requests.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns the current authenticated user's details.
     */
    @GetMapping
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "roles", user.getRoles()
        ));
    }

    /**
     * Update user password.
     */
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(
            Authentication authentication,
            @RequestBody Map<String, String> request) {
        
        // Implementation would go here
        return ResponseEntity.ok(Map.of("message", "Password updated"));
    }
}
