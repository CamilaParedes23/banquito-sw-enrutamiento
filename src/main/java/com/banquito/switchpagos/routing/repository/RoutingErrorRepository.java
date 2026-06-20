package com.banquito.switchpagos.routing.repository;

import com.banquito.switchpagos.routing.model.RoutingError;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoutingErrorRepository extends JpaRepository<RoutingError, UUID> {

    Optional<RoutingError> findByLineId(UUID lineId);
}
