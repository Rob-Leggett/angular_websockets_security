package au.com.example.websocket.api.exception;

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }
}
