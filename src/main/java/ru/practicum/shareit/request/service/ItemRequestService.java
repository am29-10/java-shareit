package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest create(ItemRequest itemRequest, Long userId);

    List<ItemRequestWithAnswersDto> getAllByRequestorId(Long id, Integer from, Integer size);


    ItemRequestWithAnswersDto getById(Long userId, Long id);

    List<ItemRequestWithAnswersDto> getAll(Long userId, Integer from, Integer size);
}
