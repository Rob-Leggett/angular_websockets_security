package au.com.example.websocket.service.notification;


import au.com.example.websocket.constant.Constants;
import au.com.example.websocket.service.notification.model.NotificationDetail;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(Constants.SERVICE_NOTIFICATION)
public class NotificationServiceImpl implements NotificationService {

    @Override public List<NotificationDetail> getNotifications(String username) {
        List<NotificationDetail> notifications = new ArrayList<>();
        notifications.add(new NotificationDetail(1L));
        notifications.add(new NotificationDetail(2L));
        notifications.add(new NotificationDetail(3L));

        return notifications;
    }
}
