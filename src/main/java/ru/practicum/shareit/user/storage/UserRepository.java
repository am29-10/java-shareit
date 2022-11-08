package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User create(User user);

    List<User> readAll();

    User update(Long id, User user);

    User getUserById(Long id);

    void delete(Long id);
}
