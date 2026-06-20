package com.banquito.switchpagos.routing.repository;

import com.banquito.switchpagos.routing.model.PaymentLineRouting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentLineRoutingRepository extends JpaRepository<PaymentLineRouting, UUID> {

    Optional<PaymentLineRouting> findByLineId(UUID lineId);
}
