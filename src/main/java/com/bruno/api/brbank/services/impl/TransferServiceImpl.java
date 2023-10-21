package com.bruno.api.brbank.services.impl;

import com.bruno.api.brbank.dtos.TransferDTO;
import com.bruno.api.brbank.dtos.TransferRequest;
import com.bruno.api.brbank.entities.Transfers;
import com.bruno.api.brbank.entities.User;
import com.bruno.api.brbank.repositories.TransferRepository;
import com.bruno.api.brbank.repositories.UserRepository;
import com.bruno.api.brbank.services.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final UserRepository userRepository;
    private final TransferRepository transferRepository;

    @Override
    public void createTransfer(TransferRequest request, User sender, User recipient) {
        validTransferRequest(request, sender);
        sender.setBalance(sender.getBalance().subtract(request.getValue()));
        recipient.setBalance(recipient.getBalance().add(request.getValue()));
        Transfers transfer = new Transfers();
        transfer.setValue(request.getValue());
        transfer.setRecipientId(request.getRecipient());
        transfer.setSenderId(request.getSenderId());
        transfer.setSenderName(sender.getName());
        transfer.setRecipientName(recipient.getName());
        transferRepository.save(transfer);
    }

    @Override
    public void validTransferRequest(TransferRequest transferRequest, User sender) {
        if(sender.getBalance().doubleValue() < transferRequest.getValue().doubleValue()){
            throw new IllegalArgumentException("Not enough balance for the transfer");
        }
        if(transferRequest.getValue().doubleValue() == 0){
            throw new IllegalArgumentException("The transfer amount cannot be 0");
        }
        if(transferRequest.getValue().doubleValue() < 0){
            throw new IllegalArgumentException("Transactions with negative amounts are not permitted");
        }
        if(sender.getRole().toString().equals("MERCHANT")){
            throw new IllegalArgumentException("Merchant cannot send money");
        }
        if(Objects.equals(transferRequest.getRecipient(), transferRequest.getSenderId())){
            throw new IllegalArgumentException("It is not allowed to make a transaction for yourself");
        }
    }

    @Override
    public void save(TransferDTO transfers) {
        Transfers transfer = new Transfers();
        BeanUtils.copyProperties(transfers, transfer);
        transferRepository.save(transfer);
    }
}
