package ru.practicum.shareit.userTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@mail.ru")
                .build();
    }

    @Test
    void create() {
        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user);
        User user1 = userService.create(user);

        assertEquals(user1, user);

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void update() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user);
        user.setName("userUpdate");
        User user1 = userService.update(1L, user);

        assertEquals(user1.getName(), user.getName());

        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateFail() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(1L, user));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void get() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        User user1 = userService.get(1L);

        assertEquals(user1, user);

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void delete() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        userService.delete(1L);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.get(1L));

        verify(userRepository, times(2)).findById(anyLong());
        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void getAll() {
        Mockito
                .when(userRepository.findAll((Pageable) any()))
                .thenReturn(new PageImpl<>(List.of(user)));
        List<User> users = userService.getAll(0, 10);

        assertEquals(users.size(), 1);

        verify(userRepository, times(1)).findAll((Pageable) any());
    }
}