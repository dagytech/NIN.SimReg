package com.dagytech.simreg.repository;

import com.dagytech.simreg.model.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffUserRepository extends JpaRepository<StaffUser, String> {
}
