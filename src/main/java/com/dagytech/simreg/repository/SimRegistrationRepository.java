package com.dagytech.simreg.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dagytech.simreg.model.SimRegistration;

// inteface segregation/ hii ni interface ndogo yenye jukumu moja tu kuhusu sim registration

public interface SimRegistrationRepository extends JpaRepository<SimRegistration, Long> {

    Optional<SimRegistration> findByReference(String reference);

    List<SimRegistration> findByCustomerNin(String nin);

    boolean existsByCustomerIdAndMobileNumberAndStatusNot(Long customerId, String mobileNumber, String status);

    long countByAgentIdAndCreatedAtAfter(Long agentId, LocalDateTime since);
}
