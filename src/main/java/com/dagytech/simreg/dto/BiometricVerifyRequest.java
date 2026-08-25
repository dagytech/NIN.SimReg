package com.dagytech.simreg.dto;

public class BiometricVerifyRequest {
    private String nin;
    private String fingerprintScan;   // mock - alama ya kidole iliyosomwa kwa sasa

    public String getNin() { return nin; }
    public void setNin(String nin) { this.nin = nin; }
    public String getFingerprintScan() { return fingerprintScan; }
    public void setFingerprintScan(String fingerprintScan) { this.fingerprintScan = fingerprintScan; }
}
