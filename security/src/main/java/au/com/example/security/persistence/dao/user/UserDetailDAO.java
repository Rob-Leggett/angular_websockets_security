package au.com.example.security.persistence.dao.user;

import au.com.example.security.persistence.dao.user.entity.UserEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailDAO {
    UserEntity loadUser(String email) throws UsernameNotFoundException;
}
