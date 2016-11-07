package au.com.example.restful.persistence.dao.user.exceptions;

public class ChangePasswordException extends RuntimeException {
    private static final long serialVersionUID = 2978320980462051681L;

    public ChangePasswordException(String message) {
        super(message);
    }
}
