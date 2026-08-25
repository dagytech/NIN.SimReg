package com.dagytech.simreg.exception;

public class DuplicateRegistrationException extends RuntimeException {
    public DuplicateRegistrationException(String mobileNumber) {
        super("Namba hii tayari ina usajili unaoendelea au uliokamilika: " + mobileNumber);
    }
}
