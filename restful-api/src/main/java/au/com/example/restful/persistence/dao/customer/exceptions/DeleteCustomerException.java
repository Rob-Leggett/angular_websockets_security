package au.com.example.restful.persistence.dao.customer.exceptions;

public class DeleteCustomerException extends RuntimeException {
    private static final long serialVersionUID = 5506033991116551044L;

    public DeleteCustomerException(String message) {
        super(message);
    }
}
