package au.com.example.restful.persistence.dao.customer.exceptions;

public class UpdateCustomerException extends RuntimeException {
    private static final long serialVersionUID = -8819614967396644975L;

    public UpdateCustomerException(String message) {
        super(message);
    }
}
