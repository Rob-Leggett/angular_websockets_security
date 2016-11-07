package au.com.example.restful.persistence.dao.user.exceptions;

public class CreateUserException extends RuntimeException {
    private static final long serialVersionUID = -8712337910120088658L;

    public CreateUserException(String message) {
        super(message);
    }
}
