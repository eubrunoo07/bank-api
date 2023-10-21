package com.bruno.api.brbank.services.impl;

import com.bruno.api.brbank.dtos.TransferRequest;
import com.bruno.api.brbank.dtos.UserDTO;
import com.bruno.api.brbank.entities.User;
import com.bruno.api.brbank.enums.UserRole;
import com.bruno.api.brbank.repositories.UserRepository;
import com.bruno.api.brbank.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<User> findById(Long senderId) {
        return repository.findById(senderId);
    }

    @Override
    public boolean existsByCpfOrEmail(String cpf, String email) {
        return repository.existsByCpfOrEmail(cpf, email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return repository.existsByCpf(cpf);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Optional<User> findByCpf(String cpf) {
        return repository.findByCpf(cpf);
    }

    @Override
    public void delete(User user) {
        repository.delete(user);
    }

    @Override
    public User dtoToEntity(UserDTO dto){
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setRole(UserRole.valueOf(dto.getRole()));
        return user;
    }

    @Override
    public void validDtoToSave(UserDTO dto) {
        if(!Objects.equals(UserRole.COMMON_USER.toString(), dto.getRole()) && !Objects.equals(UserRole.MERCHANT.toString(), dto.getRole()) && !Objects.equals(UserRole.ADMIN.toString(), dto.getRole())){
            throw new IllegalArgumentException("Wrong user type, the types are: MERCHANT, COMMON_USER or ADMIN");
        }
        if(!dto.getName().matches("[A-Z][a-z].* [A-Z][a-z].*")){
            throw new IllegalArgumentException("The name must contain at least the first and middle name");
        }
        if(repository.existsByCpfOrEmail(dto.getCpf(), dto.getEmail())){
            throw new IllegalArgumentException("This email or CPF already has an associated record");
        }
        if(dto.getBalance() == null){
            dto.setBalance(BigDecimal.valueOf(0));
        }
        if(dto.getBalance().doubleValue() < 0){
            throw new IllegalArgumentException("You cannot have a negative amount on your balance");
        }
    }

    @Override
    public void updateValidation(UserDTO dto) {
        if(!Objects.equals(UserRole.COMMON_USER.toString(), dto.getRole()) && !Objects.equals(UserRole.MERCHANT.toString(), dto.getRole())){
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
    }
}
