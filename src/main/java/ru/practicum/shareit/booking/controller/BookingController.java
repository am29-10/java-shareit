package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    BookingService bookingService;

    @GetMapping
    public List<Booking> getAll(@RequestHeader("X-Sharer-User-id") @Valid Long id,
                                @RequestParam(defaultValue = "ALL") State state,
                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET /bookings?state={}", state);
        if (from < 0 || size <= 0) {
            log.info("Параметры поиска введены некоректно");
            throw new IllegalArgumentException("Параметры поиска введены некоректно");
        }
        return bookingService.findAllByRenterId(id, state, PageRequest.of(from / size, size));
    }

    @GetMapping("/owner")
    public List<Booking> getAllByOwnerId(@RequestHeader("X-Sharer-User-id") @Valid Long id,
                                         @RequestParam(defaultValue = "ALL") State state,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                         @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET /owner");
        if (from < 0 || size <= 0) {
            log.info("Параметры поиска введены некоректно");
            throw new IllegalArgumentException("Параметры поиска введены некоректно");
        }
        return bookingService.findAllByOwnerId(id, state, PageRequest.of(from / size, size));
    }

    @GetMapping("/{id}")
    public Booking get(@RequestHeader("X-Sharer-User-id") long userId, @Valid @PathVariable Long id) {
        log.info("Получен запрос GET /booking/{}", id);
        return bookingService.get(id, userId);
    }

    @PostMapping
    public Booking create(@RequestHeader("X-Sharer-User-id") long userId, @Valid @RequestBody BookingDto bookingDto) {
        log.info("Получен запрос POST /items");
        Booking booking = BookingMapper.toBooking(bookingDto);
        return bookingService.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking setStatus(@RequestHeader("X-Sharer-User-id") long userId, @Valid @PathVariable Long bookingId,
                             @RequestParam boolean approved) {
        log.info("Получен запрос PATCH /bookings/{}={}", bookingId, approved);
        return bookingService.setStatus(bookingId, userId, approved);
    }
}
