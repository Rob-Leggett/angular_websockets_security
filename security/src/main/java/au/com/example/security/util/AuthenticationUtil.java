package au.com.example.security.util;

import au.com.example.security.persistence.dao.user.entity.MembershipEntity;
import au.com.example.security.persistence.dao.user.entity.UserEntity;
import au.com.example.security.service.authentication.model.TokenDetail;
import au.com.example.security.service.user.model.MembershipDetail;
import au.com.example.security.service.user.model.UserDetail;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class AuthenticationUtil {

    public static String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username = principal.toString();

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        }

        return username;
    }

    public static TokenDetail toTokenDetail(UserDetails userDetails) {
        return (userDetails == null) ? null : new TokenDetail(userDetails.getUsername());
    }

    public static UserDetails toUserDetails(UserEntity userEntity) {
        UserDetails userDetails = null;

        if (userEntity != null) {
            userDetails = new UserDetail(
                    userEntity.getEmail(),
                    userEntity.getPassword(),
                    userEntity.getFirstName(),
                    userEntity.getLastName(),
                    userEntity.isEnabled(),
                    userEntity.isAccountNonExpired(),
                    userEntity.isCredentialsNonExpired(),
                    userEntity.isAccountNonLocked(),
                    getGrantedAuthorities(userEntity.getMemberships()));
        }

        return userDetails;
    }

    public static Collection<GrantedAuthority> getGrantedAuthorities(List<MembershipEntity> memberships) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        if (memberships != null) {
            for (MembershipEntity membership : memberships) {
                authorities.add(new MembershipDetail(
                        membership.getId(),
                        membership.getPlanType() == null ? null : membership.getPlanType().name(),
                        membership.getExpire()));
            }
        }

        return authorities;
    }
}
