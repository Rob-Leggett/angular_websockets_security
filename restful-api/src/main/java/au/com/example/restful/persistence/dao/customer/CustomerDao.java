package au.com.example.restful.persistence.dao.customer;

import au.com.example.restful.api.controller.customer.model.Customer;

import java.util.List;

public interface CustomerDao {
    List<Customer> getCustomers();

    void deleteCustomer(Long id);

    void updateCustomer(Customer customer);
}
