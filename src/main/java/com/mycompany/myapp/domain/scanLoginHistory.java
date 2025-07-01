package com.mycompany.myapp.domain;

import java.time.ZonedDateTime;
import java.util.Date;
import javax.persistence.*;
import liquibase.pro.packaged.I;

@Entity
@Table(name = "scan_loginHistory")
public class scanLoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_id")
    private Long loginId;

    @Column(name = "username")
    private String username;

    @Column(name = "time_login")
    private String timeLogin;

    @Column(name = "order_id")
    private Long orderId;

    public scanLoginHistory() {}

    public scanLoginHistory(Long loginId, String username, String timeLogin, Long orderId) {
        this.loginId = loginId;
        this.username = username;
        this.timeLogin = timeLogin;
        this.orderId = orderId;
    }

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimeLogin() {
        return timeLogin;
    }

    public void setTimeLogin(String timeLogin) {
        this.timeLogin = timeLogin;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
