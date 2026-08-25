package com.dagytech.simreg.dto;

public class ApproveRegistrationRequest {
    private String reference;
    private String otpCode;

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
}
