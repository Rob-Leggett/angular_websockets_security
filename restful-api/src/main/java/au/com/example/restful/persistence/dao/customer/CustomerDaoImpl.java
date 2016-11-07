package au.com.example.restful.persistence.dao.customer;

import au.com.example.restful.api.controller.customer.model.Customer;
import au.com.example.restful.persistence.dao.customer.entity.CustomerEntity;
import au.com.example.restful.persistence.dao.customer.exceptions.DeleteCustomerException;
import au.com.example.restful.persistence.dao.customer.exceptions.UpdateCustomerException;
import au.com.example.restful.persistence.dao.customer.query.SelectCustomer;
import au.com.example.security.persistence.dao.base.BaseDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class CustomerDaoImpl extends BaseDAO implements CustomerDao {

    private static Logger log = LoggerFactory.getLogger(CustomerDaoImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getCustomers() {
        List<Customer> customers = new ArrayList<>();

        Collection<CustomerEntity> customerEntities = loadData(CustomerEntity.class, new SelectCustomer(null));

        if (customerEntities == null || customerEntities.isEmpty()) {
            log.info("No customers found");
        } else {
            for (CustomerEntity customerEntity : customerEntities) {
                customers.add(toCustomer(customerEntity));
            }
        }

        return customers;
    }

    @Override
    @Transactional(readOnly = false)
    public void updateCustomer(Customer customer) throws UpdateCustomerException {
        EntityManager entityManager = getEmf().createEntityManager();

        try {
            EntityTransaction tx = null;

            try {
                tx = entityManager.getTransaction();

                tx.begin();

                entityManager.merge(toCustomerEntity(customer));

                tx.commit();
            } catch (Exception e) {
                log.error("Exception during updating customer " + customer + ": " + e.getMessage());

                throw new UpdateCustomerException(e.getMessage());
            } finally {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCustomer(Long id) throws DeleteCustomerException {
        EntityManager entityManager = getEmf().createEntityManager();

        try {
            EntityTransaction tx = null;

            try {
                tx = entityManager.getTransaction();

                tx.begin();

                CustomerEntity customerEntity = loadDataSingle(CustomerEntity.class, new SelectCustomer(id));

                if (customerEntity == null) {
                    log.error("Customer with id " + id + " not found");

                    throw new DeleteCustomerException("Customer with id " + id + " not found");
                }

                entityManager.remove(entityManager.find(CustomerEntity.class, customerEntity.getId()));

                tx.commit();
            } catch (Exception e) {
                log.error("Exception during deleting customer " + id + ": " + e.getMessage());

                throw new DeleteCustomerException(e.getMessage());
            } finally {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
            }
        } finally {
            entityManager.close();
        }
    }

    // ======== Helpers =========

    private Customer toCustomer(CustomerEntity customerEntity) {
        Customer customer = null;

        if (customerEntity != null) {
            customer = new Customer(customerEntity.getId(), customerEntity.getFirstName(), customerEntity.getLastName());
        }

        return customer;
    }

    private CustomerEntity toCustomerEntity(Customer customer) {
        CustomerEntity entity = null;

        if (customer != null) {
            entity = new CustomerEntity(customer.getId(), customer.getFirstName(), customer.getLastName());
        }

        return entity;
    }
}
