package au.com.example.websocket.service.notification.model;

public class NotificationDetail {
    private Long id;

    public NotificationDetail(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
