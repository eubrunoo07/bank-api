package com.bruno.api.brbank.controllers;

import com.bruno.api.brbank.dtos.TransferRequest;
import com.bruno.api.brbank.dtos.UserDTO;
import com.bruno.api.brbank.dtos.UserResponseDTO;
import com.bruno.api.brbank.entities.User;
import com.bruno.api.brbank.enums.UserRole;
import com.bruno.api.brbank.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {


    public static final Long ID = 1L;
    public static final String NAME = "Bruno Silva";
    public static final String CPF = "13711695000";
    public static final String EMAIL = "bruno@gmail.com";
    public static final String PASSWORD = "12345";
    public static final UserRole USER_TYPE = UserRole.COMMON_USER;
    public static final BigDecimal BALANCE = BigDecimal.valueOf(100);

    @InjectMocks
    private UserController controller;
    private User user;
    private UserDTO dto;
    @Mock
    private UserService service;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        startUser();
    }

    @Test
    void whenSaveThenReturnCreated() {
        Mockito.when(service.save(Mockito.any())).thenReturn(user);
        ResponseEntity<?> response = controller.create(dto);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(ResponseEntity.class, response.getClass());
    }

    @Test
    void whenTransferThenReturnSuccess() {
        User sender = new User();
        BeanUtils.copyProperties(user, sender);
        sender.setRole(UserRole.COMMON_USER);

        User recipient = new User();
        recipient.setName("Wallace Silva");
        recipient.setCpf("45597409093");
        recipient.setEmail("wallace@gmail.com");
        recipient.setPassword("1234");
        recipient.setRole(UserRole.COMMON_USER);
        recipient.setBalance(BigDecimal.valueOf(0));
        recipient.setId(2L);

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSenderId(sender.getId());
        transferRequest.setRecipient(recipient.getId());
        transferRequest.setValue(BigDecimal.valueOf(23.39));

        Mockito.when(service.findById(sender.getId())).thenReturn(Optional.of(sender));
        Mockito.when(service.findById(recipient.getId())).thenReturn(Optional.of(recipient));
        ResponseEntity<?> responseEntity = controller.transferMethod(transferRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Transfer completed successfully", responseEntity.getBody());
        assertEquals(sender.getBalance().add(transferRequest.getValue()).subtract(transferRequest.getValue()), sender.getBalance());
        assertEquals(transferRequest.getValue(), recipient.getBalance());
    }

    @Test
    void whenTransferThenReturnAnErrorBecauseInsufficientBalance(){
        User sender = new User();
        BeanUtils.copyProperties(user, sender);
        sender.setRole(UserRole.COMMON_USER);
        sender.setBalance(BigDecimal.valueOf(10));

        User recipient = new User();
        recipient.setName("Wallace Silva");
        recipient.setCpf("45597409093");
        recipient.setEmail("wallace@gmail.com");
        recipient.setPassword("1234");
        recipient.setRole(UserRole.COMMON_USER);
        recipient.setBalance(BigDecimal.valueOf(0));
        recipient.setId(2L);

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSenderId(sender.getId());
        transferRequest.setRecipient(recipient.getId());
        transferRequest.setValue(BigDecimal.valueOf(20));

        Mockito.when(service.findById(sender.getId())).thenReturn(Optional.of(sender));
        Mockito.when(service.findById(recipient.getId())).thenReturn(Optional.of(recipient));
        try{
            controller.transferMethod(transferRequest);
        } catch (Exception e){
            Assertions.assertEquals("Not enough balance for the transfer", e.getMessage());
            Assertions.assertEquals(e.getClass(), IllegalArgumentException.class);
            Assertions.assertEquals(BigDecimal.ZERO, recipient.getBalance());
            Assertions.assertEquals(BigDecimal.valueOf(10), sender.getBalance());
        }
    }

    @Test
    public void testUpdateUserSuccessfully() {
        Mockito.when(service.findById(ID)).thenReturn(Optional.of(user));
        Mockito.when(service.existsByEmail(dto.getEmail())).thenReturn(false);
        Mockito.when(service.existsByCpf(dto.getCpf())).thenReturn(false);
        Mockito.when(service.save(Mockito.any(User.class))).thenReturn(user);

        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
        assertEquals(dto.getCpf(), user.getCpf());
        assertEquals(UserRole.COMMON_USER, user.getRole());
    }

    @Test
    public void whenUpdateThenReturnAnException() {
        Mockito.when(service.findById(ID)).thenReturn(Optional.of(user));
        Mockito.when(service.existsByEmail(dto.getEmail())).thenReturn(true);
        Mockito.when(service.existsByCpf(dto.getCpf())).thenReturn(false);

        try {
            controller.update(dto, ID);
        } catch (Exception e){
            Assertions.assertEquals("This email or CPF already has an associated record", e.getMessage());
            Assertions.assertEquals(e.getClass(), IllegalArgumentException.class);
        }
    }

    @Test
    void whenDeleteThenReturnSuccess () {
        Mockito.when(service.findById(ID)).thenReturn(Optional.of(user));
        ResponseEntity<?> responseEntity = controller.delete(ID);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("User deleted successfully", responseEntity.getBody());
        Mockito.verify(service).delete(user);
    }
    @Test
    void whenDeleteThenReturnException () {
        Mockito.when(service.findById(ID)).thenReturn(Optional.of(user));
        Mockito.doThrow(new IllegalArgumentException("User not exists")).when(service).delete(user);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> controller.delete(ID));
        assertEquals("User not exists", exception.getMessage());
    }

    @Test
    void whenFindAllReturnAnListOfUsers() {

        Mockito.when(service.findAll()).thenReturn(List.of(user));

        List<User> response = controller.allUsers();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(User.class, response.get(0).getClass());
        Assertions.assertEquals(response.get(0).getId(), ID);
    }

    @Test
    void whenFindUserByIdThenReturnSuccess() {

        Mockito.when(service.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));

        ResponseEntity<UserResponseDTO> response = controller.getById(ID);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(UserResponseDTO.class, response.getBody().getClass());
    }

    private void startUser(){
        user = new User(ID, NAME, CPF, EMAIL, PASSWORD, USER_TYPE, BALANCE);
        dto = new UserDTO(ID, NAME, CPF, EMAIL, PASSWORD, BALANCE, USER_TYPE.toString());
    }
}