package au.com.example.security.persistence.dao.user.entity;

import au.com.example.security.constant.PlanType;
import au.com.example.security.constant.Constants;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = Constants.TABLE_MEMBERSHIP)
public class MembershipEntity implements Cloneable {
    private Long id;
    private PlanType planType;
    private Date expire;

    public MembershipEntity() {
        this(null, null, null);
    }

    public MembershipEntity(Long id, PlanType planType, Date expire) {
        this.id = id;
        this.planType = planType;
        this.expire = expire;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "membership_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "plan")
    @Enumerated(EnumType.STRING)
    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    @Column(name = "expire")
    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    // === Cloneable implementation

    @Override
    public MembershipEntity clone() {
        return new MembershipEntity(id, planType, expire);
    }

    // === Overrides

    @Override
    public String toString() {
        return "MembershipEntity{" +
                "id=" + id +
                ", planType=" + planType +
                ", expire=" + expire +
                '}';
    }
}
