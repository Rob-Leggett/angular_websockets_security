package au.com.example.security.persistence.dao.user;

import au.com.example.security.persistence.dao.base.BaseDAO;
import au.com.example.security.persistence.dao.user.entity.UserEntity;
import au.com.example.security.persistence.dao.user.query.SelectUser;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDetailDAOImpl extends BaseDAO implements UserDetailDAO {

    @Override
    @Transactional(readOnly = true)
    public UserEntity loadUser(String email) throws UsernameNotFoundException, DataAccessException {
        UserEntity userEntity = loadDataSingle(UserEntity.class, new SelectUser(email));

        if (userEntity == null) {
            throw new UsernameNotFoundException("User " + email + " could not be found.");
        } else {
            return userEntity;
        }
    }
}
