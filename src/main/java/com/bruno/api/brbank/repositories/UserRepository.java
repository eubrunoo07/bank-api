package com.bruno.api.brbank.repositories;

import com.bruno.api.brbank.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByCpfOrEmail(String cpf, String email);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    Optional<User> findByEmail(String email);
    Optional<User> findByCpf(String cpf);
    @Query("SELECT u FROM User u WHERE u.email = :email")
    UserDetails findByLogin(String email);
}
