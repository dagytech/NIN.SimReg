package com.dagytech.simreg.repository;

import com.dagytech.simreg.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
// inteface segregation/ hii ni interface ndogo yenye jukumu moja tu kuhusu customer


public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByNin(String nin);
}
