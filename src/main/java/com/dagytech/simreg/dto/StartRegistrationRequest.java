package com.dagytech.simreg.dto;

public class StartRegistrationRequest {
    private String nin;
    private String mobileNumber;
    private String mno;
    private String agentCode;
    private String deviceFingerprint;
    private String ipAddress;

    public String getNin() { return nin; }
    public void setNin(String nin) { this.nin = nin; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getMno() { return mno; }
    public void setMno(String mno) { this.mno = mno; }
    public String getAgentCode() { return agentCode; }
    public void setAgentCode(String agentCode) { this.agentCode = agentCode; }
    public String getDeviceFingerprint() { return deviceFingerprint; }
    public void setDeviceFingerprint(String deviceFingerprint) { this.deviceFingerprint = deviceFingerprint; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
