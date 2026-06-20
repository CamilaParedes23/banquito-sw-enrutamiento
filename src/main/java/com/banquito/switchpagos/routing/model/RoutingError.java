package com.banquito.switchpagos.routing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "\"ERROR_ENRUTAMIENTO\"")
public class RoutingError {

    @Id
    @Column(name = "\"ID_ERROR_ENRUTAMIENTO\"")
    private UUID routingErrorId;

    @Column(name = "\"ID_LINEA\"", nullable = false, unique = true)
    private UUID lineId;

    @Column(name = "\"ID_LOTE\"", nullable = false)
    private UUID batchId;

    @Column(name = "\"ID_EVENTO_ORIGEN\"")
    private UUID sourceEventId;

    @Column(name = "\"CODIGO_RECHAZO\"", nullable = false, length = 60)
    private String rejectionCode;

    @Column(name = "\"MOTIVO_RECHAZO\"", nullable = false, length = 500)
    private String rejectionReason;

    @Column(name = "\"ID_EVENTO_PUBLICADO\"", nullable = false)
    private UUID publishedEventId;

    @Column(name = "\"CLAVE_ENRUTAMIENTO_PUBLICADA\"", nullable = false, length = 120)
    private String publishedRoutingKey;

    @Column(name = "\"FECHA_CREACION\"", nullable = false)
    private OffsetDateTime createdAt;

    public RoutingError() {
    }

    public RoutingError(UUID routingErrorId) {
        this.routingErrorId = routingErrorId;
    }

    public UUID getRoutingErrorId() {
        return routingErrorId;
    }

    public void setRoutingErrorId(UUID routingErrorId) {
        this.routingErrorId = routingErrorId;
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

    public UUID getSourceEventId() {
        return sourceEventId;
    }

    public void setSourceEventId(UUID sourceEventId) {
        this.sourceEventId = sourceEventId;
    }

    public String getRejectionCode() {
        return rejectionCode;
    }

    public void setRejectionCode(String rejectionCode) {
        this.rejectionCode = rejectionCode;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof RoutingError that)) {
            return false;
        }
        return Objects.equals(routingErrorId, that.routingErrorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routingErrorId);
    }

    @Override
    public String toString() {
        return "RoutingError{" +
                "routingErrorId=" + routingErrorId +
                ", batchId=" + batchId +
                ", lineId=" + lineId +
                ", rejectionCode='" + rejectionCode + '\'' +
                '}';
    }
}
