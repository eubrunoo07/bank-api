package com.bruno.api.brbank.services;

import com.bruno.api.brbank.dtos.TransferRequest;
import com.bruno.api.brbank.dtos.UserDTO;
import com.bruno.api.brbank.entities.User;
import com.bruno.api.brbank.enums.UserRole;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface UserService {
    User save(User user);

    List<User> findAll();

    Optional<User> findById(Long senderId);

    boolean existsByCpfOrEmail(String cpf, String email);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

    void deleteById(Long id);

    Optional<User> findByEmail(String email);
    Optional<User> findByCpf(String cpf);

    void delete(User user);

    User dtoToEntity(UserDTO dto);

    void validDtoToSave(UserDTO dto);

    void updateValidation(UserDTO dto);
}
