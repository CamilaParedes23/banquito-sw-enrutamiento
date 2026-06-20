package com.banquito.switchpagos.routing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "\"ENRUTAMIENTO_LINEA_PAGO\"")
public class PaymentLineRouting {

    @Id
    @Column(name = "\"ID_ENRUTAMIENTO\"")
    private UUID routingId;

    @Column(name = "\"ID_EVENTO_ORIGEN\"")
    private UUID sourceEventId;

    @Column(name = "\"ID_LOTE\"", nullable = false)
    private UUID batchId;

    @Column(name = "\"ID_LINEA\"", nullable = false, unique = true)
    private UUID lineId;

    @Column(name = "\"ID_CORRELACION\"", nullable = false)
    private UUID correlationId;

    @Column(name = "\"NUMERO_SECUENCIA\"")
    private Integer sequenceNumber;

    @Column(name = "\"RUC_EMPRESA\"", length = 20)
    private String companyRuc;

    @Column(name = "\"NUMERO_CUENTA_MATRIZ\"", length = 40)
    private String sourceAccountNumber;

    @Column(name = "\"ID_FONDEO_CORE\"", length = 80)
    private String coreFundingId;

    @Column(name = "\"IDENTIFICACION_BENEFICIARIO\"", length = 30)
    private String beneficiaryIdentification;

    @Column(name = "\"NOMBRE_BENEFICIARIO\"", length = 120)
    private String beneficiaryName;

    @Column(name = "\"NUMERO_CUENTA_DESTINO\"", length = 40)
    private String destinationAccountNumber;

    @Column(name = "\"CODIGO_ENRUTAMIENTO\"", length = 10)
    private String routingCode;

    @Column(name = "\"MONTO\"", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "\"MONEDA\"", length = 3)
    private String currency;

    @Column(name = "\"REFERENCIA\"", length = 140)
    private String reference;

    @Column(name = "\"EMAIL_NOTIFICACION\"", length = 120)
    private String notificationEmail;

    @Column(name = "\"TIPO_ENRUTAMIENTO\"", length = 20)
    private String routingType;

    @Column(name = "\"ESTADO\"", nullable = false, length = 30)
    private String status;

    @Column(name = "\"FECHA_CREACION\"", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "\"FECHA_ACTUALIZACION\"", nullable = false)
    private OffsetDateTime updatedAt;

    public PaymentLineRouting() {
    }

    public PaymentLineRouting(UUID routingId) {
        this.routingId = routingId;
    }

    public UUID getRoutingId() {
        return routingId;
    }

    public void setRoutingId(UUID routingId) {
        this.routingId = routingId;
    }

    public UUID getSourceEventId() {
        return sourceEventId;
    }

    public void setSourceEventId(UUID sourceEventId) {
        this.sourceEventId = sourceEventId;
    }

    public UUID getBatchId() {
        return batchId;
    }

    public void setBatchId(UUID batchId) {
        this.batchId = batchId;
    }

    public UUID getLineId() {
        return lineId;
    }

    public void setLineId(UUID lineId) {
        this.lineId = lineId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getCompanyRuc() {
        return companyRuc;
    }

    public void setCompanyRuc(String companyRuc) {
        this.companyRuc = companyRuc;
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    public String getCoreFundingId() {
        return coreFundingId;
    }

    public void setCoreFundingId(String coreFundingId) {
        this.coreFundingId = coreFundingId;
    }

    public String getBeneficiaryIdentification() {
        return beneficiaryIdentification;
    }

    public void setBeneficiaryIdentification(String beneficiaryIdentification) {
        this.beneficiaryIdentification = beneficiaryIdentification;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public void setDestinationAccountNumber(String destinationAccountNumber) {
        this.destinationAccountNumber = destinationAccountNumber;
    }

    public String getRoutingCode() {
        return routingCode;
    }

    public void setRoutingCode(String routingCode) {
        this.routingCode = routingCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public String getRoutingType() {
        return routingType;
    }

    public void setRoutingType(String routingType) {
        this.routingType = routingType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof PaymentLineRouting that)) {
            return false;
        }
        return Objects.equals(routingId, that.routingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routingId);
    }

    @Override
    public String toString() {
        return "PaymentLineRouting{" +
                "routingId=" + routingId +
                ", batchId=" + batchId +
                ", lineId=" + lineId +
                ", routingCode='" + routingCode + '\'' +
                ", routingType='" + routingType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
