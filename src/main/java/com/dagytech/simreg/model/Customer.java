package com.dagytech.simreg.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nin;

    // Majina matatu - kama NIDA inavyorekodi (siyo "fullName" moja tu)
    private String firstName;
    private String middleName;
    private String lastName;

    private LocalDate dateOfBirth;
    private String phoneNumber;
    private boolean nidaVerified = false;
    private boolean biometricVerified = false;

    public Customer() {
    }

    public Customer(String nin, String firstName, String middleName, String lastName, LocalDate dateOfBirth) {
        this.nin = nin;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    public String fullName() {
        if (middleName == null || middleName.isBlank()) {
            return firstName + " " + lastName;
        }
        return firstName + " " + middleName + " " + lastName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNin() { return nin; }
    public void setNin(String nin) { this.nin = nin; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public boolean isNidaVerified() { return nidaVerified; }
    public void setNidaVerified(boolean nidaVerified) { this.nidaVerified = nidaVerified; }
    public boolean isBiometricVerified() { return biometricVerified; }
    public void setBiometricVerified(boolean biometricVerified) { this.biometricVerified = biometricVerified; }
}
