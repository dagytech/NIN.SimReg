package com.dagytech.simreg.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dagytech.simreg.model.Agent;

// inteface segregation/ hii ni interface ndogo yenye jukumu moja tu kuhusu agent

public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findByAgentCode(String agentCode);
}
