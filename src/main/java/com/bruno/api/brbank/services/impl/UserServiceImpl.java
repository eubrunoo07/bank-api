package com.bruno.api.brbank.services.impl;

import com.bruno.api.brbank.entities.User;
import com.bruno.api.brbank.repositories.UserRepository;
import com.bruno.api.brbank.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

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
}
