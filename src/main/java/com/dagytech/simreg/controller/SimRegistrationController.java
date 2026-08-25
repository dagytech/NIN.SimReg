package com.dagytech.simreg.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dagytech.simreg.dto.ApproveRegistrationRequest;
import com.dagytech.simreg.dto.BiometricVerifyRequest;
import com.dagytech.simreg.dto.CustomerProfileResponse;
import com.dagytech.simreg.dto.RegisterSimRequest;
import com.dagytech.simreg.dto.RegistrationStatusResponse;
import com.dagytech.simreg.dto.StartRegistrationRequest;
import com.dagytech.simreg.dto.VerifyNinRequest;
import com.dagytech.simreg.model.SimRegistration;
import com.dagytech.simreg.service.SimRegistrationService;

// single responsibility/  Kupokea HTTP requests na kurudisha responses TU


@RestController
@CrossOrigin
public class SimRegistrationController {

    private final SimRegistrationService service;

    @Autowired
    public SimRegistrationController(SimRegistrationService service) {
        this.service = service;
    }

    // POST /verify-nin
    @PostMapping("/verify-nin")
    public ResponseEntity<CustomerProfileResponse> verifyNin(@RequestBody VerifyNinRequest req) {
        return ResponseEntity.ok(service.verifyNin(req.getNin()));
    }

    // POST /verify-biometric (fingerprint)
    @PostMapping("/verify-biometric")
    public ResponseEntity<CustomerProfileResponse> verifyBiometric(@RequestBody BiometricVerifyRequest req) {
        return ResponseEntity.ok(service.verifyBiometric(req.getNin(), req.getFingerprintScan()));
    }

    // POST /start-registration
    @PostMapping("/start-registration")
    public ResponseEntity<RegistrationStatusResponse> startRegistration(@RequestBody StartRegistrationRequest req) {
        return ResponseEntity.ok(service.startRegistration(req));
    }

    // POST /approve-registration
    @PostMapping("/approve-registration")
    public ResponseEntity<RegistrationStatusResponse> approveRegistration(@RequestBody ApproveRegistrationRequest req) {
        return ResponseEntity.ok(service.approveRegistration(req.getReference(), req.getOtpCode()));
    }

    // POST /register-sim
    @PostMapping("/register-sim")
    public ResponseEntity<RegistrationStatusResponse> registerSim(@RequestBody RegisterSimRequest req) {
        return ResponseEntity.ok(service.registerSim(req.getReference()));
    }

    // GET /registration-status/{reference}
    @GetMapping("/registration-status/{reference}")
    public ResponseEntity<RegistrationStatusResponse> getStatus(@PathVariable String reference) {
        return ResponseEntity.ok(service.getStatus(reference));
    }

    // GET /customer-sims/{nin}
    @GetMapping("/customer-sims/{nin}")
    public ResponseEntity<List<SimRegistration>> getCustomerSims(@PathVariable String nin) {
        return ResponseEntity.ok(service.getCustomerSims(nin));
    }

    // Ziada: audit trail
    @GetMapping("/audit-trail/{reference}")
    public ResponseEntity<?> getAuditTrail(@PathVariable String reference) {
        return ResponseEntity.ok(service.getAuditTrail(reference));
    }
}
