package com.dagytech.simreg.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sim_registrations")
public class SimRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    private String mobileNumber;
    private String mno;

    // PENDING_APPROVAL, APPROVED, REGISTERED_MNO, COMPLETED, REJECTED
    private String status = "PENDING_APPROVAL";

    private LocalDateTime createdAt = LocalDateTime.now();

    public SimRegistration() {
    }

    public SimRegistration(String reference, Customer customer, Agent agent, String mobileNumber, String mno) {
        this.reference = reference;
        this.customer = customer;
        this.agent = agent;
        this.mobileNumber = mobileNumber;
        this.mno = mno;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Agent getAgent() { return agent; }
    public void setAgent(Agent agent) { this.agent = agent; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getMno() { return mno; }
    public void setMno(String mno) { this.mno = mno; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
