package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAll(@RequestParam(defaultValue = "0") int from,
                             @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET /users");
        return userService.getAll(from, size);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        log.info("Получен запрос GET /users/{}", id);
        return userService.get(id);
    }

    @PostMapping
    public User create(@RequestBody UserDto userDto) {
        log.info("Получен запрос POST /users");
        User user = UserMapper.toUser(userDto);
        return userService.create(user);
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Получен запрос PUT /users");
        User user = UserMapper.toUser(userDto);
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получен запрос DELETE /users");
        userService.delete(id);
    }

}
