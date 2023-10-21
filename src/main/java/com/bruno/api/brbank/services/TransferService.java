package com.bruno.api.brbank.services;

import com.bruno.api.brbank.dtos.TransferDTO;
import com.bruno.api.brbank.dtos.TransferRequest;
import com.bruno.api.brbank.entities.Transfers;
import com.bruno.api.brbank.entities.User;

public interface TransferService {
    void createTransfer(TransferRequest request, User sender, User recipient);
    void validTransferRequest(TransferRequest transferRequest, User sender);
    void save(TransferDTO transfers);
}
