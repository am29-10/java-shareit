package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        User createUser = userRepository.save(user);
        log.info("Пользователь с id '{}' добавлен в список", createUser.getId());
        return createUser;
    }

    @Override
    public User update(Long id, User user) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            if (user.getName() != null) {
                userOpt.get().setName(user.getName());
            }
            if (user.getEmail() != null) {
                userOpt.get().setEmail(user.getEmail());
            }
            User updateUser = userRepository.save(userOpt.get());
            log.info("Пользователь с id '{}' обновлен", updateUser.getId());
            return updateUser;
        } else {
            log.info("EntityNotFoundException (Пользователь не может быть обновлен, т.к. его нет в списке)");
            throw new EntityNotFoundException("Пользователь не может быть обновлен, т.к. его нет в списке");
        }
    }

    @Override
    public User get(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new EntityNotFoundException(String.format("Пользователя с id=%d нет в списке", userId));
        }
    }

    @Override
    public void delete(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new EntityNotFoundException(String.format("Пользователя с id=%d нет в списке", userId));
        }
    }

    @Override
    public List<User> getAll(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return userRepository.findAll(pageable).toList();
    }

}
