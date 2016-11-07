package au.com.example.websocket.api.controller.notification;

import au.com.example.websocket.service.notification.NotificationService;
import au.com.example.security.util.AuthenticationUtil;
import au.com.example.websocket.service.notification.model.NotificationDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // ===== NOTIFICATIONS ======

    @SubscribeMapping("/api/user/notifications")
    public List<NotificationDetail> notifications() {
        return notificationService.getNotifications(AuthenticationUtil.getUsername());
    }
}
