package com.bruno.api.brbank.dtos;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransferRequest {
    @NotNull(message = "The sender must be indicated")
    private Long senderId;
    @NotNull(message = "The recipient must be indicated")
    private Long recipient;
    @NotNull(message = "The transfer amount must be indicated")
    private BigDecimal value;

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRecipient() {
        return recipient;
    }

    public void setRecipient(Long recipient) {
        this.recipient = recipient;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
