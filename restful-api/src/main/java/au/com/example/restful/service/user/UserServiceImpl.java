package au.com.example.restful.service.user;

import au.com.example.restful.constant.Constants;
import au.com.example.restful.persistence.dao.user.UserDAO;
import au.com.example.restful.persistence.dao.user.exceptions.ChangePasswordException;
import au.com.example.restful.persistence.dao.user.exceptions.CreateUserException;
import au.com.example.restful.persistence.dao.user.exceptions.DeleteUserException;
import au.com.example.restful.persistence.dao.user.exceptions.UpdateUserException;
import au.com.example.restful.util.EntityConversionUtil;
import au.com.example.security.persistence.dao.user.UserDetailDAO;
import au.com.example.security.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service(Constants.SERVICE_USER)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserDetailDAO userDetailDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // === UserDetailsService implementation ===

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException, DataAccessException {
        return AuthenticationUtil.toUserDetails(userDetailDAO.loadUser(email));
    }

    // === UserDetailsManager implementation ===

    @Override
    public void changePassword(String email, String password) throws ChangePasswordException {
        userDAO.changePassword(email, password);
    }

    @Override
    public boolean userExists(String email) {
        return userDAO.userExists(email);
    }

    @Override
    public void createUser(UserDetails user) throws CreateUserException {
        userDAO.createUser(EntityConversionUtil.toUserEntity(user, passwordEncoder));
    }

    @Override
    public void updateUser(UserDetails user) throws UpdateUserException {
        userDAO.updateUser(EntityConversionUtil.toUserEntity(user, null));
    }

    @Override
    public void deleteUser(String email) throws DeleteUserException {
        userDAO.deleteUser(email);
    }

}
