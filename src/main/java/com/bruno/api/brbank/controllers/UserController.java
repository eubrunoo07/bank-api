package com.bruno.api.brbank.controllers;

import com.bruno.api.brbank.dtos.TransferRequest;
import com.bruno.api.brbank.dtos.UserDTO;
import com.bruno.api.brbank.entities.User;
import com.bruno.api.brbank.enums.UserRole;
import com.bruno.api.brbank.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/bank/users")
@CrossOrigin(value = "*")
public class UserController {
    @Autowired
    private UserService service;

    @Operation(summary = "Realizar o registro de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro desse usuário ocorreu com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválidos"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar o registro deste usuário"),
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid UserDTO dto){
        dto.setRole(dto.getRole().toUpperCase());
        validDtoToSave(dto);
        service.save(dtoToEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválidos"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao fazer a transação"),
    })
    @Operation(summary = "Realizar uma transferência para um usuário")
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

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atualização desse usuário ocorreu com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválidos"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao atualizar os dados deste usuário"),
    })
    @Operation(summary = "Realizar uma atualização nos dados de um usuário")
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody@Valid UserDTO dto, @PathVariable Long id){
        dto.setRole(dto.getRole().toUpperCase());
        User user = service.findById(id).orElseThrow(() -> new IllegalArgumentException("User not exists"));
        if(!Objects.equals(UserRole.COMMON_USER.toString(), dto.getRole()) && !Objects.equals(UserRole.MERCHANT.toString(), dto.getRole())){
            System.out.println("caiu");
            throw new IllegalArgumentException("Wrong user type, the types are: MERCHANT or USER_COMMON");
        }
        if(!dto.getName().matches("[A-Z][a-z].* [A-Z][a-z].*")){
            throw new IllegalArgumentException("The name must contain at least the first and middle name");
        }
        if(dto.getBalance() == null){
            dto.setBalance(BigDecimal.valueOf(0));
        }
        if(dto.getBalance().doubleValue() < 0){
            throw new IllegalArgumentException("You cannot have a negative amount on your balance");
        }
        if(service.existsByEmail(user.getEmail()) && service.existsByCpf(user.getCpf())){
            if(service.existsByEmail(user.getEmail()) && service.existsByCpf(user.getCpf())){
                User emailFoundedByEmail = service.findByEmail(dto.getEmail()).orElse(new User());
                User emailFoundedByCpf = service.findByCpf(dto.getCpf()).orElse(new User());

                if (emailFoundedByEmail.getId() == null && emailFoundedByCpf.getId() == null) {
                    throw new IllegalArgumentException("This email or CPF already has an associated record");
                }

                User updatedUser = new User();
                BeanUtils.copyProperties(dto, updatedUser);
                updatedUser.setId(user.getId());
                updatedUser.setRole(UserRole.valueOf(dto.getRole()));
                service.save(updatedUser);
                return ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
            }
            else{
                throw new IllegalArgumentException("This email or CPF already has an associated record");
            }
        }
        return null;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleção desse usuário ocorreu com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválidos"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao deletar este usuário"),
    })
    @Operation(summary = "Realizar a deleção de um usuário")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        User user = service.findById(id).orElseThrow(() -> new IllegalArgumentException("User not exists"));
        service.delete(user);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválidos"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao buscar este usuário"),
    })
    @Operation(summary = "Realizar a busca de todos os usuário")
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<User> allUsers(){
        return service.findAll();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválidos"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao buscar este usuário"),
    })
    @Operation(summary = "Realizar a busca de um usuário por ID")
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id){
        return service.findById(id).orElseThrow(() -> new IllegalArgumentException("User not exists"));
    }

    private User dtoToEntity(UserDTO dto){
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setRole(UserRole.valueOf(dto.getRole()));
        return user;
    }

    private UserDTO entityToDto(User user){
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        dto.setId(user.getId());
        dto.setRole(user.getRole().toString());
        return dto;
    }

    private void validDtoToSave(UserDTO dto) {
        if(!Objects.equals(UserRole.COMMON_USER.toString(), dto.getRole()) && !Objects.equals(UserRole.MERCHANT.toString(), dto.getRole())){
            System.out.println("caiu");
            throw new IllegalArgumentException("Wrong user type, the types are: MERCHANT or COMMON_USER");
        }
        if(!dto.getName().matches("[A-Z][a-z].* [A-Z][a-z].*")){
            throw new IllegalArgumentException("The name must contain at least the first and middle name");
        }
        if(service.existsByCpfOrEmail(dto.getCpf(), dto.getEmail())){
            throw new IllegalArgumentException("This email or CPF already has an associated record");
        }
        if(dto.getBalance() == null){
            dto.setBalance(BigDecimal.valueOf(0));
        }
        if(dto.getBalance().doubleValue() < 0){
            throw new IllegalArgumentException("You cannot have a negative amount on your balance");
        }
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
        if(sender.getRole().toString().equals("MERCHANT")){
            throw new IllegalArgumentException("Merchant cannot send money");
        }
        if(transferRequest.getRecipient() == transferRequest.getSenderId()){
            throw new IllegalArgumentException("It is not allowed to make a transaction for yourself");
        }
    }

}
