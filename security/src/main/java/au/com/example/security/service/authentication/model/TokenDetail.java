package au.com.example.security.service.authentication.model;

import java.io.Serializable;

public class TokenDetail implements Serializable {

    private String email;

    public TokenDetail() {
        this(null);
    }

    public TokenDetail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
