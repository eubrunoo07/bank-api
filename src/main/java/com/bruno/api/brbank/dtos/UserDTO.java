package com.bruno.api.brbank.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

@JsonPropertyOrder({"key", "name", "cpf", "email", "password", "userType"})
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
    @NotBlank(message = "User type cannot be null or empty")
    private String userType;

    public UserDTO(Long id, String name, String cpf, String email, String password, BigDecimal balance, String userType) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.userType = userType;
    }

    public UserDTO(){

    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
