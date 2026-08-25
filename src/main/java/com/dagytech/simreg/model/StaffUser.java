package com.dagytech.simreg.model;

import jakarta.persistence.*;

/**
 * StaffUser = mtumiaji wa ndani (Agent au Admin) mwenye ruhusa ya ku-login
 * kupitia JWT - tofauti na mteja (Customer) anayetumia API Key + HMAC
 * signature badala ya login (angalia maelezo ya "access point" kwenye README).
 */
@Entity
@Table(name = "staff_users")
public class StaffUser {

    @Id
    private String username;

    private String password;   // BCrypt hash
    private String role;       // ADMIN au AGENT
    private String agentCode;  // inaunganisha na Agent (kwa role=AGENT tu)

    public StaffUser() {
    }

    public StaffUser(String username, String password, String role, String agentCode) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.agentCode = agentCode;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getAgentCode() { return agentCode; }
    public void setAgentCode(String agentCode) { this.agentCode = agentCode; }
}
