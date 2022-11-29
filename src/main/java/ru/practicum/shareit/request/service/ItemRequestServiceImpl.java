package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequest create(ItemRequest itemRequest, Long userId) {
        validate(itemRequest, userId);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(userRepository.findById(userId).get());
        ItemRequest itemRequestCreated = itemRequestRepository.save(itemRequest);
        log.info("Запрос с id = '{}' добавлен в список", itemRequestCreated.getId());
        return itemRequestCreated;
    }

    @Override
    public List<ItemRequestWithAnswersDto> getAllByRequestorId(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.info("Пользователь отсутствует в списке");
            throw new EntityNotFoundException(String.format("Пользователь с id=%d отсутствует в списке", userId));
        }
        return itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(userId)
                .stream()
                .map(ItemRequestMapper::toItemRequestWithAnswersDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestWithAnswersDto> getAll(Long userId, Pageable pageable) {
        return itemRequestRepository.findAll().stream()
                .filter(itemRequest -> !itemRequest.getRequestor().getId().equals(userId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .map(ItemRequestMapper::toItemRequestWithAnswersDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestWithAnswersDto getById(Long userId, Long id) {
        if (userRepository.findById(userId).isEmpty()) {
            log.info("EntityNotFoundException (Несуществующий пользователь)");
            throw new EntityNotFoundException("Несуществующий пользователь");
        }
        if (itemRequestRepository.findById(id).isEmpty()) {
            log.info("EntityNotFoundException (Несуществующий запрос)");
            throw new EntityNotFoundException("Несуществующий запрос");
        }
        return ItemRequestMapper
                .toItemRequestWithAnswersDto(itemRequestRepository.findById(id).get());
    }

    private void validate(ItemRequest itemRequest, Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.info("EntityNotFoundException (Несуществующий пользователь)");
            throw new EntityNotFoundException("Несуществующий пользователь");
        }
        if (itemRequest.getDescription() == null || itemRequest.getDescription().isBlank()) {
            log.info("IllegalArgumentException (Пустое описание)");
            throw new IllegalArgumentException("Пустое описание");
        }
    }
}
