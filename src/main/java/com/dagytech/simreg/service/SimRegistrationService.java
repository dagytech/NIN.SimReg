// single responsibility/  Mantiki ya biashara (business logic) TU


package com.dagytech.simreg.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dagytech.simreg.dto.CustomerProfileResponse;
import com.dagytech.simreg.dto.RegistrationStatusResponse;
import com.dagytech.simreg.dto.StartRegistrationRequest;
import com.dagytech.simreg.exception.BiometricMismatchException;
import com.dagytech.simreg.exception.DuplicateRegistrationException;
import com.dagytech.simreg.exception.InvalidOtpException;
import com.dagytech.simreg.exception.NinNotFoundException;
import com.dagytech.simreg.exception.RateLimitExceededException;
import com.dagytech.simreg.exception.RegistrationNotFoundException;
import com.dagytech.simreg.model.Agent;
import com.dagytech.simreg.model.AuditLog;
import com.dagytech.simreg.model.Customer;
import com.dagytech.simreg.model.DeviceInfo;
import com.dagytech.simreg.model.NidaMockRecord;
import com.dagytech.simreg.model.RegistrationApproval;
import com.dagytech.simreg.model.SimRegistration;
import com.dagytech.simreg.repository.AgentRepository;
import com.dagytech.simreg.repository.AuditLogRepository;
import com.dagytech.simreg.repository.CustomerRepository;
import com.dagytech.simreg.repository.DeviceInfoRepository;
import com.dagytech.simreg.repository.NidaMockRecordRepository;
import com.dagytech.simreg.repository.RegistrationApprovalRepository;
import com.dagytech.simreg.repository.SimRegistrationRepository;

@Service
public class SimRegistrationService {

    private final CustomerRepository customerRepository;
    private final AgentRepository agentRepository;
    private final SimRegistrationRepository simRegistrationRepository;
    private final RegistrationApprovalRepository approvalRepository;
    private final DeviceInfoRepository deviceInfoRepository;
    private final AuditLogRepository auditLogRepository;
    private final NidaMockRecordRepository nidaMockRecordRepository;
    private final ExternalServicesMock externalServices;

    @Autowired
    public SimRegistrationService(CustomerRepository customerRepository, AgentRepository agentRepository,
                                   SimRegistrationRepository simRegistrationRepository,
                                   RegistrationApprovalRepository approvalRepository,
                                   DeviceInfoRepository deviceInfoRepository,
                                   AuditLogRepository auditLogRepository,
                                   NidaMockRecordRepository nidaMockRecordRepository,
                                   ExternalServicesMock externalServices) {
        this.customerRepository = customerRepository;
        this.agentRepository = agentRepository;
        this.simRegistrationRepository = simRegistrationRepository;
        this.approvalRepository = approvalRepository;
        this.deviceInfoRepository = deviceInfoRepository;
        this.auditLogRepository = auditLogRepository;
        this.nidaMockRecordRepository = nidaMockRecordRepository;
        this.externalServices = externalServices;
    }

    // ============ 1) POST /verify-nin ============
    @Transactional
    public CustomerProfileResponse verifyNin(String nin) {
        NidaMockRecord nidaRecord = nidaMockRecordRepository.findById(nin)
                .orElseThrow(() -> new NinNotFoundException(nin));

        Customer customer = customerRepository.findByNin(nin)
                .orElseGet(() -> new Customer(nin, nidaRecord.getFirstName(), nidaRecord.getMiddleName(),
                        nidaRecord.getLastName(), nidaRecord.getDateOfBirth()));

        customer.setFirstName(nidaRecord.getFirstName());
        customer.setMiddleName(nidaRecord.getMiddleName());
        customer.setLastName(nidaRecord.getLastName());
        customer.setDateOfBirth(nidaRecord.getDateOfBirth());
        customer.setNidaVerified(true);
        customerRepository.save(customer);

        log("CUSTOMER", nin, "VERIFY_NIN", "SYSTEM", "NIN imethibitishwa dhidi ya NIDA");
        return toProfileResponse(customer);
    }

    // ============ 1b) POST /verify-biometric (fingerprint) ============
    @Transactional
    public CustomerProfileResponse verifyBiometric(String nin, String fingerprintScan) {
        Customer customer = customerRepository.findByNin(nin)
                .orElseThrow(() -> new NinNotFoundException(nin));

        if (!customer.isNidaVerified()) {
            throw new IllegalArgumentException("Thibitisha NIN kwanza kabla ya biometric verification");
        }

        NidaMockRecord nidaRecord = nidaMockRecordRepository.findById(nin)
                .orElseThrow(() -> new NinNotFoundException(nin));

        // Mock matching - kwenye mfumo halisi hii ingekuwa biometric matching algorithm
        // (mfano minutiae matching) dhidi ya template iliyohifadhiwa NIDA.
        boolean matches = nidaRecord.getFingerprintTemplate() != null
                && nidaRecord.getFingerprintTemplate().equalsIgnoreCase(fingerprintScan);

        if (!matches) {
            log("CUSTOMER", nin, "BIOMETRIC_MISMATCH", "SYSTEM", "Alama ya kidole haifanani na kumbukumbu za NIDA");
            throw new BiometricMismatchException("Alama ya kidole (fingerprint) haifanani na kumbukumbu za NIDA");
        }

        customer.setBiometricVerified(true);
        customerRepository.save(customer);

        log("CUSTOMER", nin, "VERIFY_BIOMETRIC", "SYSTEM", "Fingerprint imethibitishwa");
        return toProfileResponse(customer);
    }

    // ============ 2) POST /start-registration ============
    @Transactional
    public RegistrationStatusResponse startRegistration(StartRegistrationRequest req) {
        Customer customer = customerRepository.findByNin(req.getNin())
                .orElseThrow(() -> new NinNotFoundException(req.getNin()));

        if (!customer.isNidaVerified()) {
            throw new IllegalArgumentException("NIN bado haijathibitishwa - piga /verify-nin kwanza");
        }
        if (!customer.isBiometricVerified()) {
            throw new IllegalArgumentException("Biometric (fingerprint) bado haijathibitishwa - piga /verify-biometric kwanza");
        }
        if (req.getMobileNumber() == null || req.getMobileNumber().isBlank()) {
            throw new IllegalArgumentException("mobileNumber inahitajika");
        }

        boolean duplicate = simRegistrationRepository
                .existsByCustomerIdAndMobileNumberAndStatusNot(customer.getId(), req.getMobileNumber(), "REJECTED");
        if (duplicate) {
            throw new DuplicateRegistrationException(req.getMobileNumber());
        }

        Agent agent = null;
        if (req.getAgentCode() != null && !req.getAgentCode().isBlank()) {
            agent = agentRepository.findByAgentCode(req.getAgentCode())
                    .orElseThrow(() -> new IllegalArgumentException("Agent hajapatikana: " + req.getAgentCode()));
            if (!"ACTIVE".equals(agent.getStatus())) {
                throw new IllegalArgumentException("Agent huyu amesimamishwa (SUSPENDED)");
            }
            long todayCount = simRegistrationRepository
                    .countByAgentIdAndCreatedAtAfter(agent.getId(), LocalDateTime.now().toLocalDate().atStartOfDay());
            if (todayCount >= agent.getDailyLimit()) {
                throw new RateLimitExceededException(
                        "Agent " + agent.getAgentCode() + " amefikia kikomo cha usajili kwa siku (" + agent.getDailyLimit() + ")");
            }
        }

        if (req.getDeviceFingerprint() != null) {
            long recentAttempts = deviceInfoRepository.countByDeviceFingerprintAndRecordedAtAfter(
                    req.getDeviceFingerprint(), LocalDateTime.now().minusMinutes(10));
            if (recentAttempts >= 5) {
                throw new RateLimitExceededException("Majaribio mengi kutoka kifaa hiki ndani ya dakika 10 zilizopita");
            }
        }

        String reference = UUID.randomUUID().toString();
        SimRegistration reg = new SimRegistration(reference, customer, agent, req.getMobileNumber(), req.getMno());
        simRegistrationRepository.save(reg);

        String otp = String.format("%06d", new Random().nextInt(999999));
        RegistrationApproval approval = new RegistrationApproval(reg, otp, LocalDateTime.now().plusMinutes(5));
        approvalRepository.save(approval);

        if (req.getDeviceFingerprint() != null) {
            deviceInfoRepository.save(new DeviceInfo(reg, req.getDeviceFingerprint(), req.getIpAddress()));
        }

        externalServices.sendSms(req.getMobileNumber(), "OTP yako ya usajili wa SIM ni: " + otp + " (inaisha muda dakika 5)");

        log("SIM_REGISTRATION", reference, "START_REGISTRATION",
                agent != null ? agent.getAgentCode() : "SELF_SERVICE", "OTP: " + otp + " (kwa majaribio tu)");

        return toStatusResponse(reg);
    }

    // ============ 3) POST /approve-registration ============
    @Transactional
    public RegistrationStatusResponse approveRegistration(String reference, String otpCode) {
        SimRegistration reg = simRegistrationRepository.findByReference(reference)
                .orElseThrow(() -> new RegistrationNotFoundException(reference));

        RegistrationApproval approval = approvalRepository.findBySimRegistrationId(reg.getId())
                .orElseThrow(() -> new IllegalArgumentException("Hakuna OTP iliyotengenezwa kwa usajili huu"));

        if (approval.isApproved()) {
            throw new InvalidOtpException("Usajili huu tayari umeidhinishwa");
        }
        if (LocalDateTime.now().isAfter(approval.getOtpExpiresAt())) {
            throw new InvalidOtpException("OTP imeisha muda - anza upya usajili");
        }
        if (!approval.getOtpCode().equals(otpCode)) {
            throw new InvalidOtpException("OTP siyo sahihi");
        }

        approval.setApproved(true);
        approval.setApprovedAt(LocalDateTime.now());
        approvalRepository.save(approval);

        reg.setStatus("APPROVED");
        simRegistrationRepository.save(reg);

        log("SIM_REGISTRATION", reference, "APPROVE_REGISTRATION", "CUSTOMER", "Mteja amethibitisha kwa OTP");
        return toStatusResponse(reg);
    }

    // ============ 4) POST /register-sim ============
    @Transactional
    public RegistrationStatusResponse registerSim(String reference) {
        SimRegistration reg = simRegistrationRepository.findByReference(reference)
                .orElseThrow(() -> new RegistrationNotFoundException(reference));

        if (!"APPROVED".equals(reg.getStatus())) {
            throw new IllegalArgumentException("Usajili lazima uwe APPROVED kabla ya kusajili SIM (sasa: " + reg.getStatus() + ")");
        }

        externalServices.registerWithMno(reg.getMno(), reg.getMobileNumber());
        reg.setStatus("REGISTERED_MNO");
        simRegistrationRepository.save(reg);
        log("SIM_REGISTRATION", reference, "REGISTER_SIM", "SYSTEM", "Imesajiliwa na " + reg.getMno());

        externalServices.submitToRegulator(reference);
        reg.setStatus("COMPLETED");
        simRegistrationRepository.save(reg);
        log("SIM_REGISTRATION", reference, "SUBMIT_REGULATOR", "SYSTEM", "Imewasilishwa kwa TCRA");

        externalServices.sendSms(reg.getMobileNumber(),
                "Hongera! SIM yako " + reg.getMobileNumber() + " imesajiliwa kikamilifu.");
        log("SIM_REGISTRATION", reference, "SMS_SENT", "SYSTEM", "SMS ya uthibitisho imetumwa");

        return toStatusResponse(reg);
    }

    // ============ 5) GET /registration-status/{reference} ============
    public RegistrationStatusResponse getStatus(String reference) {
        SimRegistration reg = simRegistrationRepository.findByReference(reference)
                .orElseThrow(() -> new RegistrationNotFoundException(reference));
        return toStatusResponse(reg);
    }

    // ============ 6) GET /customer-sims/{nin} ============
    public List<SimRegistration> getCustomerSims(String nin) {
        return simRegistrationRepository.findByCustomerNin(nin);
    }

    public List<AuditLog> getAuditTrail(String reference) {
        return auditLogRepository.findByEntityRefOrderByOccurredAtDesc(reference);
    }

    // ---- Helper methods ----

    private RegistrationStatusResponse toStatusResponse(SimRegistration reg) {
        return new RegistrationStatusResponse(reg.getReference(), reg.getStatus(), reg.getMobileNumber(),
                reg.getMno(), reg.getCustomer().fullName());
    }

    private CustomerProfileResponse toProfileResponse(Customer c) {
        return new CustomerProfileResponse(c.getNin(), c.getFirstName(), c.getMiddleName(), c.getLastName(),
                c.getDateOfBirth(), c.isNidaVerified(), c.isBiometricVerified());
    }

    private void log(String entityType, String entityRef, String action, String performedBy, String details) {
        auditLogRepository.save(new AuditLog(entityType, entityRef, action, performedBy, details));
    }
}
