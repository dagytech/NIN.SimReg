package com.dagytech.simreg.dto;

public class RegistrationStatusResponse {
    private String reference;
    private String status;
    private String mobileNumber;
    private String mno;
    private String customerFullName;

    public RegistrationStatusResponse(String reference, String status, String mobileNumber,
                                       String mno, String customerFullName) {
        this.reference = reference;
        this.status = status;
        this.mobileNumber = mobileNumber;
        this.mno = mno;
        this.customerFullName = customerFullName;
    }

    public String getReference() { return reference; }
    public String getStatus() { return status; }
    public String getMobileNumber() { return mobileNumber; }
    public String getMno() { return mno; }
    public String getCustomerFullName() { return customerFullName; }
}
