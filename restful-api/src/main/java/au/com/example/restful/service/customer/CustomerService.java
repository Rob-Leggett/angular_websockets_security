package au.com.example.restful.service.customer;

import au.com.example.restful.api.controller.customer.model.Customer;

import java.util.List;

public interface CustomerService {
	List<Customer> getCustomers();

    void deleteCustomer(Long id);

    void updateCustomer(Customer customer);
}
