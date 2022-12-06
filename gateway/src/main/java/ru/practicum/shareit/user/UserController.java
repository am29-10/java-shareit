package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                         @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET /users");
        return userClient.getUsers(from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@Valid @PathVariable Long id) {
        log.info("Получен запрос GET /users/{}", id);
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос POST /users");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Valid @PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Получен запрос PUT /users");
        return userClient.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@Valid @PathVariable Long id) {
        log.info("Получен запрос DELETE /users");
        return userClient.deleteUser(id);
    }
}
