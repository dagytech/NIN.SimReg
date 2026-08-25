package com.dagytech.simreg.exception;

public class RegistrationNotFoundException extends RuntimeException {
    public RegistrationNotFoundException(String reference) {
        super("Usajili haujapatikana kwa reference: " + reference);
    }
}
