package com.dagytech.simreg.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registration_approvals")
public class RegistrationApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "sim_registration_id")
    private SimRegistration simRegistration;

    private String otpCode;
    private LocalDateTime otpExpiresAt;
    private boolean approved = false;
    private LocalDateTime approvedAt;

    public RegistrationApproval() {
    }

    public RegistrationApproval(SimRegistration simRegistration, String otpCode, LocalDateTime otpExpiresAt) {
        this.simRegistration = simRegistration;
        this.otpCode = otpCode;
        this.otpExpiresAt = otpExpiresAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SimRegistration getSimRegistration() { return simRegistration; }
    public void setSimRegistration(SimRegistration simRegistration) { this.simRegistration = simRegistration; }
    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
    public LocalDateTime getOtpExpiresAt() { return otpExpiresAt; }
    public void setOtpExpiresAt(LocalDateTime otpExpiresAt) { this.otpExpiresAt = otpExpiresAt; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
}
