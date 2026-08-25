package com.dagytech.simreg.dto;

import java.time.LocalDate;

/**
 * Hii inaonyesha WAZI majina yote matatu + tarehe ya kuzaliwa baada ya
 * NIN/biometric verification - ili mteja aone taarifa zake halisi kutoka
 * NIDA kabla ya kuendelea na usajili (badala ya kumruhusu ajiandikie mwenyewe).
 */
public class CustomerProfileResponse {
    private String nin;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dateOfBirth;
    private boolean nidaVerified;
    private boolean biometricVerified;

    public CustomerProfileResponse(String nin, String firstName, String middleName, String lastName,
                                    LocalDate dateOfBirth, boolean nidaVerified, boolean biometricVerified) {
        this.nin = nin;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.nidaVerified = nidaVerified;
        this.biometricVerified = biometricVerified;
    }

    public String getNin() { return nin; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public boolean isNidaVerified() { return nidaVerified; }
    public boolean isBiometricVerified() { return biometricVerified; }
}
