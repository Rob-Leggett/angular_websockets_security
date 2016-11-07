package au.com.example.restful.persistence.dao.user;


import au.com.example.restful.persistence.dao.user.exceptions.ChangePasswordException;
import au.com.example.restful.persistence.dao.user.exceptions.CreateUserException;
import au.com.example.restful.persistence.dao.user.exceptions.DeleteUserException;
import au.com.example.restful.persistence.dao.user.exceptions.UpdateUserException;
import au.com.example.security.persistence.dao.user.entity.UserEntity;

public interface UserDAO {

    void changePassword(String email, String password) throws ChangePasswordException;

    boolean userExists(String email);

    void createUser(UserEntity user) throws CreateUserException;

    void updateUser(UserEntity user) throws UpdateUserException;

    void deleteUser(String email) throws DeleteUserException;
}
