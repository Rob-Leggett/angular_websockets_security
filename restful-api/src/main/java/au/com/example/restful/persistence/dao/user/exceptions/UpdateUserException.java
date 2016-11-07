package au.com.example.restful.persistence.dao.user.exceptions;

public class UpdateUserException extends RuntimeException {
    private static final long serialVersionUID = -8819614967396644975L;

    public UpdateUserException(String message) {
        super(message);
    }
}
