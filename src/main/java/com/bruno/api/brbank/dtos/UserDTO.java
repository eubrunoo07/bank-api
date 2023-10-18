package com.bruno.api.brbank.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

@JsonPropertyOrder({"key", "name", "cpf", "email", "password", "userType"})
@Getter@Setter
public class UserDTO {
    @JsonProperty("key")
    @JsonIgnore
    private Long id;
    @NotBlank(message = "Name cannot be null or empty")
    private String name;
    @NotBlank(message = "CPF cannot be null or empty")
    @CPF(message = "CPF is invalid")
    private String cpf;
    @NotBlank(message = "Email cannot be null or empty")
    @Email(message = "Email is invalid")
    private String email;
    @NotBlank(message = "Password cannot be null or empty")
    private String password;
    private BigDecimal balance;
    @NotBlank(message = "User role cannot be null or empty")
    private String role;

    public UserDTO(Long id, String name, String cpf, String email, String password, BigDecimal balance, String role) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.role = role;
    }

    public UserDTO(){

    }
}
