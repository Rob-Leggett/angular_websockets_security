package au.com.example.restful.util;

import au.com.example.security.constant.PlanType;
import au.com.example.security.persistence.dao.user.entity.MembershipEntity;
import au.com.example.security.persistence.dao.user.entity.UserEntity;
import au.com.example.security.service.user.model.MembershipDetail;
import au.com.example.security.service.user.model.UserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EntityConversionUtil {

    private static Logger log = LoggerFactory.getLogger(EntityConversionUtil.class);

    public static UserEntity toUserEntity(UserDetails userDetails, PasswordEncoder passwordEncoder) {
        UserEntity userEntity = null;

        if (userDetails != null) {
            if (userDetails instanceof UserDetail) {
                UserDetail user = (UserDetail) userDetails;

                userEntity = new UserEntity(
                        user.getUsername(),
                        passwordEncoder == null ? user.getPassword() : passwordEncoder.encode(user.getPassword()),
                        user.getFirstName(),
                        user.getLastName(),
                        getSecurityRoles(user.getAuthorities()),
                        user.isEnabled(),
                        user.isAccountNonExpired(),
                        user.isCredentialsNonExpired(),
                        user.isAccountNonLocked());
            } else {
                log.error("Only supports " + UserDetail.class + " all other classes unsupported");
            }
        }

        return userEntity;
    }

    public static List<MembershipEntity> getSecurityRoles(Collection<GrantedAuthority> authorities) {
        List<MembershipEntity> memberships = new ArrayList<>();

        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (authority instanceof MembershipDetail) {
                    MembershipDetail membership = (MembershipDetail) authority;

                    memberships.add(new MembershipEntity(
                            membership.getId(),
                            membership.getPlan() == null ? null : PlanType.valueOf(membership.getPlan()),
                            membership.getExpires()));
                }
            }
        }

        return memberships;
    }

}
