package com.bruno.api.brbank.entities;

import com.bruno.api.brbank.dtos.TransferRequest;
import com.bruno.api.brbank.enums.TransferStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "transfers_tb")
@Getter@Setter
public class Transfers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String senderName;
    @Column
    private Long senderId;
    @Column
    private String recipientName;
    @Column
    private Long recipientId;
    @Column
    private BigDecimal value;
}
