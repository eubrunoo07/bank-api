package com.bruno.api.brbank.controllers;

import com.bruno.api.brbank.dtos.AuthenticationDTO;
import com.bruno.api.brbank.dtos.TransferRequest;
import com.bruno.api.brbank.dtos.UserDTO;
import com.bruno.api.brbank.entities.User;
import com.bruno.api.brbank.enums.UserRole;
import com.bruno.api.brbank.services.TransferService;
import com.bruno.api.brbank.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank/users")
@CrossOrigin(value = "*")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final TransferService transferService;
    private final AuthenticationManager manager;

    @Operation(summary = "Realizar o registro de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro desse usuário ocorreu com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválidos"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar o registro deste usuário"),
    })
    @PostMapping("/register")
    public ResponseEntity<?> create(@RequestBody @Valid UserDTO dto){
        String encryptedPassword = new BCryptPasswordEncoder().encode(dto.getPassword());
        dto.setRole(dto.getRole().toUpperCase());
        dto.setPassword(encryptedPassword);
        service.validDtoToSave(dto);
        service.save(service.dtoToEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationDTO dto){
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
        var auth = manager.authenticate(usernamePassword);

        return ResponseEntity.status(HttpStatus.OK).body("User logged in successfully");
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
        transferService.createTransfer(transferRequest, sender, recipient);
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
    public void update(@RequestBody@Valid UserDTO dto, @PathVariable Long id){
        dto.setRole(dto.getRole().toUpperCase());
        User user = service.findById(id).orElseThrow(() -> new IllegalArgumentException("User not exists"));
        service.updateValidation(dto);
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
                ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
            }
            else{
                throw new IllegalArgumentException("This email or CPF already has an associated record");
            }
        }
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

}
