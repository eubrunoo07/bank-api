package com.bruno.api.brbank.controllers;

import com.bruno.api.brbank.dtos.TransferRequest;
import com.bruno.api.brbank.dtos.UserDTO;
import com.bruno.api.brbank.entities.User;
import com.bruno.api.brbank.enums.UserType;
import com.bruno.api.brbank.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/bank/users")
@CrossOrigin(value = "*")
public class UserController {
    @Autowired
    private UserService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid UserDTO dto){
        if(!dto.getName().matches("[A-Z][a-z].* [A-Z][a-z].*")){
            throw new IllegalArgumentException("The name must contain at least the first and middle name");
        }
        if(service.existsByCpfOrEmail(dto.getCpf(), dto.getEmail())){
            throw new IllegalArgumentException("This email or CPF already has an associated record");
        }
        if(dto.getBalance() == null){
            dto.setBalance(BigDecimal.valueOf(0));
        }
        if(!dto.getUserType().equals("COMMON_USER") && !dto.getUserType().equals("MERCHANT")){
            throw new IllegalArgumentException("This type of user does not exist");
        }
        service.save(dtoToEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferMethod(@RequestBody @Valid TransferRequest transferRequest){
        User sender = service.findById(transferRequest.getSenderId()).orElseThrow(() -> new IllegalArgumentException("This sender ID does not exist in our system"));
        User recipient = service.findById(transferRequest.getRecipient()).orElseThrow(() -> new IllegalArgumentException("This recipient ID does not exist in our system"));
        validTransferRequest(transferRequest, sender);
        sender.setBalance(sender.getBalance().subtract(transferRequest.getValue()));
        recipient.setBalance(recipient.getBalance().add(transferRequest.getValue()));
        service.save(sender);
        service.save(recipient);
        return ResponseEntity.status(HttpStatus.OK).body("Transfer completed successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody@Valid UserDTO dto, @PathVariable Long id){
        User user = service.findById(id).orElseThrow(() -> new IllegalArgumentException("User not exists"));
        if(service.existsByEmail(user.getEmail()) && service.existsByCpf(user.getCpf())){
            if(service.findByEmail(dto.getEmail()).get().getId() == user.getId() && service.findByCpf(dto.getCpf()).get().getId() == user.getId()){
                User updatedUser = new User();
                BeanUtils.copyProperties(dto, updatedUser);
                updatedUser.setId(user.getId());
                updatedUser.setType(UserType.valueOf(dto.getUserType()));
                service.save(updatedUser);
                return ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
            }
            else{
                throw new IllegalArgumentException("This email or CPF already has an associated record");
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        User user = service.findById(id).orElseThrow(() -> new IllegalArgumentException("User not exists"));
        service.delete(user);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<User> allUsers(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id){
        return service.findById(id).orElseThrow(() -> new IllegalArgumentException("User not exists"));
    }

    private User dtoToEntity(UserDTO dto){
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setType(UserType.valueOf(dto.getUserType()));
        return user;
    }

    private UserDTO entityToDto(User user){
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        dto.setId(user.getId());
        dto.setUserType(user.getType().toString());
        return dto;
    }

    private static void validTransferRequest(TransferRequest transferRequest, User sender) {
        if(sender.getBalance().doubleValue() < transferRequest.getValue().doubleValue()){
            throw new IllegalArgumentException("Not enough balance for the transfer");
        }
        if(transferRequest.getValue().doubleValue() == 0){
            throw new IllegalArgumentException("The transfer amount cannot be 0");
        }
        if(transferRequest.getValue().doubleValue() < 0){
            throw new IllegalArgumentException("Transactions with negative amounts are not permitted");
        }
        if(sender.getType().toString().equals("MERCHANT")){
            throw new IllegalArgumentException("Merchant cannot send money");
        }
        if(transferRequest.getRecipient() == transferRequest.getSenderId()){
            throw new IllegalArgumentException("It is not allowed to make a transaction for yourself");
        }
    }

}
