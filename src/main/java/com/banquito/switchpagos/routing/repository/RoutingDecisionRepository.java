package com.banquito.switchpagos.routing.repository;

import com.banquito.switchpagos.routing.model.RoutingDecision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoutingDecisionRepository extends JpaRepository<RoutingDecision, UUID> {

    Optional<RoutingDecision> findByLineId(UUID lineId);
}
