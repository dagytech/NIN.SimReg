package com.dagytech.simreg.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dagytech.simreg.model.RegistrationApproval;
// inteface segregation/ hii ni interface ndogo yenye jukumu moja tu kuhusu registration approval


public interface RegistrationApprovalRepository extends JpaRepository<RegistrationApproval, Long> {
    Optional<RegistrationApproval> findBySimRegistrationId(Long simRegistrationId);
}
