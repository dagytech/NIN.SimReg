package com.dagytech.simreg.model;

import com.dagytech.simreg.security.FingerprintEncryptionConverter;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Hii INASIMAMISHA (simulate) database ya NIDA - ikiwa na "fingerprintTemplate"
 * inayowakilisha alama ya kidole iliyohifadhiwa NIDA wakati wa usajili wa kwanza
 * wa kitambulisho. Mteja anapotoa fingerprint scan wakati wa usajili wa SIM,
 * tunalinganisha na hii "template" (mock matching - kwenye mfumo halisi hii
 * ingekuwa biometric matching algorithm halisi kupitia NIDA API).
 */
@Entity
@Table(name = "nida_mock_records")
public class NidaMockRecord {

    @Id
    private String nin;

    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dateOfBirth;

    // @Convert: hii field itafichwa (encrypted) KABLA ya kuandikwa database,
    // na kufunguliwa (decrypted) kiotomatiki inaposomwa - Hibernate inafanya
    // hivi PEKEE, code nyingine yote (service, controller) haioni tofauti yoyote.
    @Convert(converter = FingerprintEncryptionConverter.class)
    @Column(length = 500)
    private String fingerprintTemplate;   // mock - mfano "FP-TEMPLATE-0001"

    public NidaMockRecord() {
    }

    public NidaMockRecord(String nin, String firstName, String middleName, String lastName,
                           LocalDate dateOfBirth, String fingerprintTemplate) {
        this.nin = nin;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.fingerprintTemplate = fingerprintTemplate;
    }

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
    public String getFingerprintTemplate() { return fingerprintTemplate; }
    public void setFingerprintTemplate(String fingerprintTemplate) { this.fingerprintTemplate = fingerprintTemplate; }
}
