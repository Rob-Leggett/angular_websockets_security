package au.com.example.restful.persistence.dao.user;


import au.com.example.restful.persistence.dao.user.exceptions.ChangePasswordException;
import au.com.example.restful.persistence.dao.user.exceptions.CreateUserException;
import au.com.example.restful.persistence.dao.user.exceptions.DeleteUserException;
import au.com.example.restful.persistence.dao.user.exceptions.UpdateUserException;
import au.com.example.restful.persistence.dao.user.query.UpdatePassword;
import au.com.example.security.persistence.dao.base.BaseDAO;
import au.com.example.security.persistence.dao.user.entity.UserEntity;
import au.com.example.security.persistence.dao.user.query.SelectUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@Repository
public class UserDAOImpl extends BaseDAO implements UserDAO {
    private static Logger log = LoggerFactory.getLogger(UserDAOImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = false)
    public void changePassword(String username, String password) throws ChangePasswordException {
        try {
            String encodedPassword = passwordEncoder.encode(password);

            int rowsUpdated = updateDeleteDataSingle(new UpdatePassword(username, encodedPassword));

            log.debug("Number of rows updated: " + rowsUpdated);
        } catch (Exception e) {
            log.error("Unable to change password for user: " + username + " because " + e.getMessage());

            throw new ChangePasswordException("Unable to change password: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userExists(String email) {
        boolean userExists = false;

        try {
            UserEntity user = loadDataSingle(UserEntity.class, new SelectUser(email));

            if (user != null) {
                userExists = true;
            }
        } catch (Exception e) {
            log.error("Unable to determine if user " + email + " exists: " + e.getMessage());
        }

        return userExists;
    }

    @Override
    @Transactional(readOnly = false)
    public void createUser(UserEntity user) throws CreateUserException {
        EntityManager entityManager = getEmf().createEntityManager();

        try {
            EntityTransaction tx = null;

            try {
                tx = entityManager.getTransaction();

                tx.begin();

                entityManager.persist(user);

                tx.commit();
            } catch (Exception e) {
                log.error("Exception during creating user " + user + ": " + e.getMessage());

                throw new CreateUserException(e.getMessage());
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
    public void updateUser(UserEntity user) throws UpdateUserException {
        EntityManager entityManager = getEmf().createEntityManager();

        try {
            EntityTransaction tx = null;

            try {
                tx = entityManager.getTransaction();

                tx.begin();

                entityManager.merge(user);

                tx.commit();
            } catch (Exception e) {
                log.error("Exception during updating user " + user + ": " + e.getMessage());

                throw new UpdateUserException(e.getMessage());
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
    public void deleteUser(String email) throws DeleteUserException {
        EntityManager entityManager = getEmf().createEntityManager();

        try {
            EntityTransaction tx = null;

            try {
                tx = entityManager.getTransaction();

                tx.begin();

                UserEntity userEntity = loadDataSingle(UserEntity.class, new SelectUser(email));

                if (userEntity == null) {
                    log.error("User with username " + email + " not found");

                    throw new DeleteUserException("User with username " + email + " not found");
                }

                entityManager.remove(entityManager.find(UserEntity.class, userEntity.getEmail()));

                tx.commit();
            } catch (Exception e) {
                log.error("Exception during deleting user " + email + ": " + e.getMessage());

                throw new DeleteUserException(e.getMessage());
            } finally {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
            }
        } finally {
            entityManager.close();
        }
    }
}
