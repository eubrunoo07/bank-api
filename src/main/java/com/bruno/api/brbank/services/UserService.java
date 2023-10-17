package com.bruno.api.brbank.services;

import com.bruno.api.brbank.entities.User;

import java.util.List;
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
}
