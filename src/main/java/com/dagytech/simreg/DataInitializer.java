package com.dagytech.simreg;

import com.dagytech.simreg.model.Agent;
import com.dagytech.simreg.model.NidaMockRecord;
import com.dagytech.simreg.model.StaffUser;
import com.dagytech.simreg.repository.AgentRepository;
import com.dagytech.simreg.repository.NidaMockRecordRepository;
import com.dagytech.simreg.repository.StaffUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Data ya MAJARIBIO tu - NIN 3 za mfano (kuiga NIDA, zikiwa na majina matatu
 * na fingerprint template ya mock), wakala 1 wa mfano, na StaffUser za login
 * (ADMIN na AGENT) kwa ajili ya kujaribu JWT (/staff/login) na Session
 * (/admin-panel/login).
 *
 * Kwa kujaribu biometric verification, tumia fingerprintScan ILE ILE
 * iliyoonyeshwa hapa (mfano "FP-TEMPLATE-0001") kwenye ukurasa wa UI.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final NidaMockRecordRepository nidaMockRecordRepository;
    private final AgentRepository agentRepository;
    private final StaffUserRepository staffUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(NidaMockRecordRepository nidaMockRecordRepository, AgentRepository agentRepository,
                            StaffUserRepository staffUserRepository, PasswordEncoder passwordEncoder) {
        this.nidaMockRecordRepository = nidaMockRecordRepository;
        this.agentRepository = agentRepository;
        this.staffUserRepository = staffUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (nidaMockRecordRepository.count() == 0) {
            nidaMockRecordRepository.save(new NidaMockRecord("19900101-12345-00001-12",
                    "Innocent", "Petro", "Mwamafupa", LocalDate.of(1990, 1, 1), "FP-TEMPLATE-0001"));
            nidaMockRecordRepository.save(new NidaMockRecord("19850515-67890-00002-34",
                    "Asha", "Bakari", "Juma", LocalDate.of(1985, 5, 15), "FP-TEMPLATE-0002"));
            nidaMockRecordRepository.save(new NidaMockRecord("19951220-11223-00003-56",
                    "Baraka", "Elias", "Mushi", LocalDate.of(1995, 12, 20), "FP-TEMPLATE-0003"));
            System.out.println("Data ya mfano ya NIDA imeongezwa. Fingerprint za majaribio:");
            System.out.println("  19900101-12345-00001-12 -> FP-TEMPLATE-0001");
            System.out.println("  19850515-67890-00002-34 -> FP-TEMPLATE-0002");
            System.out.println("  19951220-11223-00003-56 -> FP-TEMPLATE-0003");
        }

        if (agentRepository.findByAgentCode("AGT001").isEmpty()) {
            agentRepository.save(new Agent("AGT001", "Neema Wakala"));
            System.out.println("Agent wa majaribio ameongezwa: AGT001");
        }

        if (staffUserRepository.findById("admin").isEmpty()) {
            staffUserRepository.save(new StaffUser("admin", passwordEncoder.encode("admin123"), "ADMIN", null));
            System.out.println("Staff Admin ameongezwa: username=admin, password=admin123 (JWT: /staff/login, Session: /admin-panel/login)");
        }
        if (staffUserRepository.findById("agent1").isEmpty()) {
            staffUserRepository.save(new StaffUser("agent1", passwordEncoder.encode("agent123"), "AGENT", "AGT001"));
            System.out.println("Staff Agent ameongezwa: username=agent1, password=agent123");
        }
    }
}

