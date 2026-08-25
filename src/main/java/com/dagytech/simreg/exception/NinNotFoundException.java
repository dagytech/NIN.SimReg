package com.dagytech.simreg.exception;

public class NinNotFoundException extends RuntimeException {
    public NinNotFoundException(String nin) {
        super("NIN haipatikani kwenye kumbukumbu za NIDA: " + nin);
    }
}
