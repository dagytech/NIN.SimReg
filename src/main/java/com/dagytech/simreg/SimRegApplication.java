package com.dagytech.simreg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimRegApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimRegApplication.class, args);
        System.out.println("SIM Registration API iko tayari (successfully by dagytech)");
    }
}
