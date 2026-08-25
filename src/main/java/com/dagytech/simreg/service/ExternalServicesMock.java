package com.dagytech.simreg.service;

import org.springframework.stereotype.Component;
// single responsibility/  Ku-simulate NIDA/MNO/Regulator/SMS TU

@Component
public class ExternalServicesMock {

    public boolean registerWithMno(String mno, String mobileNumber) {
        System.out.println("[MNO SIMULATION] Kusajili " + mobileNumber + " na " + mno + "... ✅ Imefanikiwa");
        return true;
    }

    public boolean submitToRegulator(String reference) {
        System.out.println("[REGULATOR SIMULATION] Kuwasilisha usajili " + reference + " kwa TCRA... ✅ Imepokelewa");
        return true;
    }

    public void sendSms(String phoneNumber, String message) {
        System.out.println("[SMS SIMULATION] Kwenda " + phoneNumber + ": \"" + message + "\"");
    }
}
