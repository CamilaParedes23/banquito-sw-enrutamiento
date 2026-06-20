package com.banquito.switchpagos.routing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "\"DECISION_ENRUTAMIENTO\"")
public class RoutingDecision {

    @Id
    @Column(name = "\"ID_DECISION\"")
    private UUID decisionId;

    @Column(name = "\"ID_LINEA\"", nullable = false, unique = true)
    private UUID lineId;

    @Column(name = "\"ID_LOTE\"", nullable = false)
    private UUID batchId;

    @Column(name = "\"TIPO_ENRUTAMIENTO\"", nullable = false, length = 20)
    private String routingType;

    @Column(name = "\"CODIGO_ENRUTAMIENTO\"", nullable = false, length = 10)
    private String routingCode;

    @Column(name = "\"NOMBRE_INSTITUCION_DESTINO\"", length = 120)
    private String destinationInstitutionName;

    @Column(name = "\"ID_EVENTO_PUBLICADO\"", nullable = false)
    private UUID publishedEventId;

    @Column(name = "\"CLAVE_ENRUTAMIENTO_PUBLICADA\"", nullable = false, length = 120)
    private String publishedRoutingKey;

    @Column(name = "\"FECHA_DECISION\"", nullable = false)
    private OffsetDateTime decidedAt;

    public RoutingDecision() {
    }

    public RoutingDecision(UUID decisionId) {
        this.decisionId = decisionId;
    }

    public UUID getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(UUID decisionId) {
        this.decisionId = decisionId;
    }

    public UUID getLineId() {
        return lineId;
    }

    public void setLineId(UUID lineId) {
        this.lineId = lineId;
    }

    public UUID getBatchId() {
        return batchId;
    }

    public void setBatchId(UUID batchId) {
        this.batchId = batchId;
    }

    public String getRoutingType() {
        return routingType;
    }

    public void setRoutingType(String routingType) {
        this.routingType = routingType;
    }

    public String getRoutingCode() {
        return routingCode;
    }

    public void setRoutingCode(String routingCode) {
        this.routingCode = routingCode;
    }

    public String getDestinationInstitutionName() {
        return destinationInstitutionName;
    }

    public void setDestinationInstitutionName(String destinationInstitutionName) {
        this.destinationInstitutionName = destinationInstitutionName;
    }

    public UUID getPublishedEventId() {
        return publishedEventId;
    }

    public void setPublishedEventId(UUID publishedEventId) {
        this.publishedEventId = publishedEventId;
    }

    public String getPublishedRoutingKey() {
        return publishedRoutingKey;
    }

    public void setPublishedRoutingKey(String publishedRoutingKey) {
        this.publishedRoutingKey = publishedRoutingKey;
    }

    public OffsetDateTime getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(OffsetDateTime decidedAt) {
        this.decidedAt = decidedAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof RoutingDecision that)) {
            return false;
        }
        return Objects.equals(decisionId, that.decisionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(decisionId);
    }

    @Override
    public String toString() {
        return "RoutingDecision{" +
                "decisionId=" + decisionId +
                ", batchId=" + batchId +
                ", lineId=" + lineId +
                ", routingType='" + routingType + '\'' +
                ", routingCode='" + routingCode + '\'' +
                '}';
    }
}
