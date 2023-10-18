package com.bruno.api.brbank.services.impl;

import com.bruno.api.brbank.dtos.UserDTO;
import com.bruno.api.brbank.entities.User;
import com.bruno.api.brbank.enums.UserRole;
import com.bruno.api.brbank.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class UserServiceImplTest {

    public static final Long ID = 1L;
    public static final String NAME = "Bruno Silva";
    public static final String CPF = "13711695000";
    public static final String EMAIL = "bruno@gmail.com";
    public static final String PASSWORD = "12345";
    public static final UserRole USER_TYPE = UserRole.COMMON_USER;
    public static final BigDecimal BALANCE = BigDecimal.valueOf(100);
    @InjectMocks
    private UserServiceImpl service;
    @Mock
    private UserRepository repository;

    private User user;

    private UserDTO dto;
    private Optional<User> optionalUser;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        startUser();
    }

    @Test
    void whenSaveThenReturnSuccess() {

        Mockito.when(repository.save(Mockito.any())).thenReturn(user);
        User response = service.save(user);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(User.class, response.getClass());
        Assertions.assertEquals(ID, response.getId());
    }

    @Test
    void whenSaveThenReturnError() {

        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(optionalUser);
        try{
            service.save(user);
        } catch (Exception e){
            Assertions.assertEquals(DataIntegrityViolationException.class, e.getClass());
            Assertions.assertEquals("This email or CPF already has an associated record", e.getClass());
        }
    }

    @Test
    void whenFindAllThenReturnAnListOfUsers() {

        Mockito.when(repository.findAll()).thenReturn(List.of(user));
        List<User> response = service.findAll();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(User.class, response.get(0).getClass());
        Assertions.assertEquals(ID, response.get(0).getId());
    }

    @Test
    void whenFindByIdThenReturnAnUserInstance() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(optionalUser);
        User response = service.findById(ID).orElseThrow(() -> new IllegalArgumentException("User not exists"));
        Assertions.assertEquals(User.class, response.getClass());
        Assertions.assertEquals(ID, response.getId());
        Assertions.assertEquals(NAME, response.getName());
        Assertions.assertEquals(CPF, response.getCpf());
        Assertions.assertEquals(EMAIL, response.getEmail());
        Assertions.assertEquals(PASSWORD, response.getPassword());
        Assertions.assertEquals(USER_TYPE, response.getType());
        Assertions.assertEquals(BALANCE, response.getBalance());
    }

    @Test
    void whenFindByIdThenReturnAnObjectNotFoundException(){
        Mockito.when(repository.findById(Mockito.anyLong())).thenThrow(new IllegalArgumentException("User not exists"));
        try{
            service.findById(ID);
        } catch (Exception e){
            Assertions.assertEquals(IllegalArgumentException.class, e.getClass());
            Assertions.assertEquals(e.getMessage(), "User not exists");
        }
    }

    @Test
    void deleteWithSuccess() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(optionalUser);
        Mockito.doNothing().when(repository).deleteById(Mockito.anyLong());
        service.deleteById(ID);
        Mockito.verify(repository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    void deleteWithUserNotFound(){
        Mockito.when(repository.findById(Mockito.anyLong())).thenThrow(new IllegalArgumentException("User not exists"));
        try {
            service.deleteById(ID);
        } catch (Exception e){
            Assertions.assertEquals(IllegalArgumentException.class, e.getClass());
            Assertions.assertEquals("User not exists", e.getMessage());
        }
    }



    private void startUser(){
        user = new User(ID, NAME, CPF, EMAIL, PASSWORD, USER_TYPE, BALANCE);
        dto = new UserDTO(ID, NAME, CPF, EMAIL, PASSWORD, BALANCE, USER_TYPE.toString());
        optionalUser = Optional.of(new User(ID, NAME, CPF, EMAIL, PASSWORD, USER_TYPE, BALANCE));
    }
}