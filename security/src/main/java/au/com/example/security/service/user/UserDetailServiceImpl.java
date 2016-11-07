package au.com.example.security.service.user;


import au.com.example.security.constant.Constants;
import au.com.example.security.persistence.dao.user.UserDetailDAO;
import au.com.example.security.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service(Constants.SERVICE_USER_DETAIL)
public class UserDetailServiceImpl implements UserDetailService {

    @Autowired
    private UserDetailDAO userDetailDAO;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, DataAccessException {
        return AuthenticationUtil.toUserDetails(userDetailDAO.loadUser(email));
    }
}
