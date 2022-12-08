package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-id") long userId,
                                         @Valid @RequestBody RequestDto requestDto) {
        log.info("Получен запрос POST /requests");
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByRequestorId(@RequestHeader("X-Sharer-User-id") long userId,
                                                               @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                               @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET /requests");
        return requestClient.getRequestsByRequestor(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-id") long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                         @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET /requests/all");
        return requestClient.getRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-id") long userId,
                                             @PathVariable Long requestId) {
        log.info("Получен запрос GET /requests/{}", requestId);
        return requestClient.getRequest(userId, requestId);
    }
}
