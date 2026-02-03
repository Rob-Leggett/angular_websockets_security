package au.com.example.controller;

import au.com.example.model.Notification;
import au.com.example.model.User;
import au.com.example.repository.NotificationRepository;
import au.com.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Notification Controller - Modern replacement for NotificationController.
 * 
 * CRITICAL: This is the WebSocket controller that requires authentication.
 * 
 * Handles:
 * - Subscription to user notifications
 * - Real-time notification delivery
 * 
 * Security is enforced by:
 * 1. WebSocketTokenInterceptor (validates token on CONNECT)
 * 2. WebSocketSecurityConfig (requires auth for SUBSCRIBE)
 */
@Controller
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationController(NotificationRepository notificationRepository,
                                  UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Handles subscription to /app/user/notifications.
     * Returns the user's unread notifications on subscribe.
     * 
     * The Authentication is automatically injected by Spring Security
     * after WebSocketTokenInterceptor validates the token.
     */
    @SubscribeMapping("/user/notifications")
    public List<Notification> subscribeToNotifications(Authentication authentication) {
        logger.debug("User subscribing to notifications: {}", authentication.getName());

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Notification> notifications = notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(user.getId());

        logger.debug("Returning {} notifications for user {}", notifications.size(), user.getEmail());

        return notifications;
    }

    /**
     * Handle incoming messages (if needed for future features).
     */
    @MessageMapping("/notifications/read")
    public void markAsRead(Long notificationId, Authentication authentication) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}
