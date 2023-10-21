package com.bruno.api.brbank.dtos;

import com.bruno.api.brbank.enums.TransferStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter@Setter
@JsonPropertyOrder({"key", "senderName", "senderId", "recipientName", "recipientId", "value"})
public class TransferDTO {
    @JsonProperty("key")
    private Long id;
    private String senderName;
    private Long senderId;
    private String recipientName;
    private Long recipientId;
    private BigDecimal value;
}
