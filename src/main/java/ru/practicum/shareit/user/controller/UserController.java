package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Получен запрос GET /users");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User get(@Valid @PathVariable Long id) {
        log.info("Получен запрос GET /users/{}", id);
        return userService.get(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос POST /users");
        User user = UserMapper.toUser(userDto);
        return userService.create(user);
    }

    @PatchMapping("/{id}")
    public User update(@Valid @PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Получен запрос PUT /users");
        User user = UserMapper.toUser(userDto);
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@Valid @PathVariable Long id) {
        log.info("Получен запрос DELETE /users");
        userService.delete(id);
    }

}
