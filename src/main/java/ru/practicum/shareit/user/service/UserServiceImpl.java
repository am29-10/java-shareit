package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        checkEmail(user);
        if (userRepository.getUserById(user.getId()) != null) {
            throw new ValidationException("Пользователь с таким id уже есть в базе");
        }
        User createUser = userRepository.create(user);
        log.info("Пользователь с id '{}' добавлен в список", createUser.getId());
        return createUser;
    }

    @Override
    public User update(Long id, User user) {
        checkEmail(user);
        if (userRepository.getUserById(id) != null) {
            User updateUser = userRepository.update(id, user);
            log.info("Пользователь с id '{}' обновлен", updateUser.getId());
            return updateUser;
        } else {
            log.info("EntityNotFoundException (Пользователь не может быть обновлен, т.к. его нет в списке)");
            throw new EntityNotFoundException("Пользователь не может быть обновлен, т.к. его нет в списке");
        }
    }

    @Override
    public User get(Long userId) {
        if (userRepository.getUserById(userId) != null) {
            return userRepository.getUserById(userId);
        } else {
            throw new EntityNotFoundException(String.format("Пользователя с id=%d нет в списке", userId));
        }
    }

    @Override
    public void delete(Long userId) {
        if (userRepository.getUserById(userId) != null) {
            userRepository.delete(userId);
        } else {
            throw new EntityNotFoundException(String.format("Пользователя с id=%d нет в списке", userId));
        }
    }

    @Override
    public List<User> getAll() {
        return userRepository.readAll();
    }

    public void checkEmail(User user) {
        for (User user1 : getAll()) {
            if (user1.getEmail().equals(user.getEmail())) {
                throw new EmailException("EmailException (Пользователь не может быть создан из-за несоответствия " +
                        "уникальности email адреса");
            }
        }
    }
}
