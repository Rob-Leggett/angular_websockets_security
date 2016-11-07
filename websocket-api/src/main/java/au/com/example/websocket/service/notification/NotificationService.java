package au.com.example.websocket.service.notification;

import au.com.example.websocket.service.notification.model.NotificationDetail;

import java.util.List;

public interface NotificationService {
    List<NotificationDetail> getNotifications(String username);
}
