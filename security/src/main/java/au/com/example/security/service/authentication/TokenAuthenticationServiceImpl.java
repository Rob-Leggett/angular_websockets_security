package au.com.example.security.service.authentication;

import au.com.example.security.constant.Constants;
import au.com.example.security.persistence.dao.user.UserDetailDAO;
import au.com.example.security.service.authentication.model.TokenDetail;
import au.com.example.security.spring.security.handler.TokenHandler;
import au.com.example.security.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service(Constants.SERVICE_TOKEN_AUTH)
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

    //private static final long TEN_DAYS = 1000 * 60 * 60 * 24 * 10;

    @Autowired
    private TokenHandler tokenHandler;

    @Autowired
    private UserDetailDAO userDetailDao;

    @Override
    public String createToken(Authentication authentication) {
        final UserDetails user = (UserDetails)authentication.getPrincipal();
        //user.setExpires(System.currentTimeMillis() + TEN_DAYS);
        return tokenHandler.createTokenForUser(AuthenticationUtil.toTokenDetail(user));
    }

    @Override
    public Authentication getAuthentication(String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null) {
            if (token != null) {
                final TokenDetail tokenDetail = tokenHandler.parseUserFromToken(token);
                if (tokenDetail != null) {
                    UserDetails details = AuthenticationUtil.toUserDetails(userDetailDao.loadUser(tokenDetail.getEmail()));
                    authentication = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                }
            }
        }

        return authentication;
    }
}