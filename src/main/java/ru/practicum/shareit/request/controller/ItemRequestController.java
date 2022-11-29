package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.dto.ItemRequestWithoutAnswersDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@AllArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequest create(@RequestHeader("X-Sharer-User-id") long userId,
                              @Valid @RequestBody ItemRequestWithoutAnswersDto itemRequestDto) {
        log.info("Получен запрос POST /requests");
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        return itemRequestService.create(itemRequest, userId);
    }

    @GetMapping
    public List<ItemRequestWithAnswersDto> getAllByRequestorId(@RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос GET /requests");
        return itemRequestService.getAllByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithAnswersDto> getAll(@RequestHeader("X-Sharer-User-id") long userId,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                     @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET /requests/all");
        if (from < 0 || size <= 0) {
            log.info("Параметры поиска введены некоректно");
            throw new IllegalArgumentException("Параметры поиска введены некоректно");
        }
        return itemRequestService.getAll(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithAnswersDto getById(@RequestHeader("X-Sharer-User-id") long userId,
                                             @PathVariable Long requestId) {
        log.info("Получен запрос GET /requests/{}", requestId);
        return itemRequestService.getById(userId, requestId);
    }
}
