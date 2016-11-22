package au.com.example.restful.api.controller.user;

import au.com.example.restful.api.controller.user.model.SimpleUserDetail;
import au.com.example.security.service.user.model.UserDetail;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public SimpleUserDetail retrieveUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserDetail user = (UserDetail) userDetails;

        return new SimpleUserDetail(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAuthorities());
    }
}
