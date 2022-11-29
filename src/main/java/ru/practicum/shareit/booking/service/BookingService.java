package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking create(Booking booking, Long bookerId);

    Booking setStatus(Long id, Long userId, boolean approved);

    Booking get(Long id, Long userId);

    List<Booking> findAllByRenterId(Long id, State state, Pageable pageable);

    List<Booking> findAllByOwnerId(Long id, State state, Pageable pageable);
}
