package au.com.example.security.service.user.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Date;

public class MembershipDetail implements GrantedAuthority {

    private Long id;
    private String plan;
    private Date expires;

    public MembershipDetail() {
        this(null, null, null);
    }

    public MembershipDetail(Long id, String plan, Date expires) {
        this.id = id;
        this.plan = plan;
        this.expires = expires;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    // ========== Overrides ============


    @Override
    public String getAuthority() {
        return plan;
    }
}
