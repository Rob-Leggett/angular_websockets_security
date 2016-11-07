package au.com.example.websocket.api.model.contact;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class Email {
    @NotNull(message = "Cannot be null")
    @Size(min = 1, message = "Cannot be blank")
    @Pattern(regexp = ".+@.+\\.[a-z]+", message = "Invalid email address")
    private String from;

    @NotNull(message = "Cannot be null")
    private Subject subject;

    @NotNull(message = "Cannot be null")
    @Size(min = 1, message = "Cannot be blank")
    private String message;

    // === Getters & Setters

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
