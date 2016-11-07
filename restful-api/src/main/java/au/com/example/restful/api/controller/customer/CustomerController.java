package au.com.example.restful.api.controller.customer;

import au.com.example.restful.api.controller.customer.model.Customer;
import au.com.example.restful.service.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/customers/retrieve", method = RequestMethod.GET, produces = "application/json")
    public List<Customer> getCustomers() {
        return customerService.getCustomers();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public void saveCustomer(@RequestBody Customer customer) {
        customerService.updateCustomer(customer);
    }
}
