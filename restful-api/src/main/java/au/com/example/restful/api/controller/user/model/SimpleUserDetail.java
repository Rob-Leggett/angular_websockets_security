package au.com.example.restful.api.controller.user.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class SimpleUserDetail {
    private String email;
    private String firstName;
    private String lastName;
    private Collection<GrantedAuthority> authorities;

    public SimpleUserDetail(String email, String firstName, String lastName, Collection<GrantedAuthority> authorities) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.authorities = authorities;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
