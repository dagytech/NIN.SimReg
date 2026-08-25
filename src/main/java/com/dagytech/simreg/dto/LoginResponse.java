package com.dagytech.simreg.dto;

public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private long expiresInMinutes;

    public LoginResponse(String token, String username, String role, long expiresInMinutes) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.expiresInMinutes = expiresInMinutes;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public long getExpiresInMinutes() { return expiresInMinutes; }
}
