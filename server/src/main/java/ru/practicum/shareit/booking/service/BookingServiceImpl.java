package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    ItemRepository itemRepository;
    UserRepository userRepository;

    @Override
    public Booking create(Booking booking, Long bookerId) {
        if (booking.getEnd().isBefore(booking.getStart())) {
            log.info("ValidationException (Нельзя завершить бронь раньше ее регистрации)");
            throw new IllegalArgumentException("Завершение брони раньше ее регистрации");
        }
        booking.setBooker(userRepository.findById(bookerId).get());
        Optional<Item> item = itemRepository.findById(booking.getItem().getId());
        if (item.isPresent()) {
            booking.setItem(item.get());
            if (!item.get().getAvailable()) {
                log.info("IllegalArgumentException (Предмет не является доступным)");
                throw new IllegalArgumentException("Предмет не является доступным");
            }
            if (item.get().getOwner().getId().equals(bookerId)) {
                throw new EntityNotFoundException("Предмет не может быть забронирован у своего обладателя");
            }
            booking.setItem(booking.getItem());
            booking.setStatus(Status.WAITING);
            return bookingRepository.save(booking);
        } else {
            log.info("Предмес с id = {} отсутствует в списке", booking.getItem().getId());
            throw new EntityNotFoundException(String.format("Предмет с id=%d отсутствует в списке",
                    booking.getItem().getId()));
        }
    }

    @Override
    public Booking setStatus(Long id, Long userId, boolean approved) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isPresent()) {
            if (booking.get().getItem().getOwner().getId().equals(userId)) {
                Status status = booking.get().getStatus();
                switch (status) {
                    case APPROVED:
                        throw new IllegalArgumentException("Статус уже был принят как 'APPROVED'");
                    case REJECTED:
                        throw new IllegalArgumentException("Статус уже был принят как 'REJECTED'");
                    case CANCELED:
                        throw new IllegalArgumentException("Статус уже был принят как 'CANCELED'");
                    case WAITING:
                        if (approved) {
                            booking.get().setStatus(Status.APPROVED);
                        } else {
                            booking.get().setStatus(Status.REJECTED);
                        }
                        return bookingRepository.save(booking.get());
                    default:
                        throw new IllegalArgumentException("Unknown status: " + status);
                }
            } else {
                throw new EntityNotFoundException(String.format("Невозможно подтвердить или отклонить бронь, т.к. " +
                        "пользователь с id = %d не является владельцем предмета с id = %d", userId, id));
            }
        } else {
            throw new EntityNotFoundException(String.format("Бронь с id=%d отсутствует в списке", id));
        }
    }

    @Override
    public Booking get(Long id, Long userId) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isPresent()) {
            if (booking.get().getBooker().getId().equals(userId) ||
                    booking.get().getItem().getOwner().getId().equals(userId)) {
                return booking.get();
            } else {
                throw new EntityNotFoundException(String.format("Невозможно выдать данные о брони, т.к. " +
                        "пользователь с id = %d не является арендатором или владельцем предмета с id = %d", userId, id));
            }
        } else {
            throw new EntityNotFoundException(String.format("Бронь с id=%d отсутствует в списке", id));
        }
    }

    @Override
    public List<Booking> findAllByRenterId(Long renterId, State state, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (userRepository.findById(renterId).isPresent()) {
            LocalDateTime now = LocalDateTime.now();
            switch (state) {
                case ALL:
                    return bookingRepository.findAllByBookerIdOrderByStartDesc(renterId, pageable).toList();
                case FUTURE:
                    return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(renterId, now, pageable)
                            .toList();
                case PAST:
                    return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(renterId, now, pageable)
                            .toList();
                case CURRENT:
                    return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(renterId,
                            now, now, pageable).toList();
                case WAITING:
                    return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(renterId, Status.WAITING,
                            pageable).toList();
                case REJECTED:
                    return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(renterId, Status.REJECTED,
                            pageable).toList();
                default:
                    throw new IllegalArgumentException("Unknown state: " + state);
            }
        } else {
            log.info("EntityNotFoundException (Невозможно найти бронь у пользователя с id = {}, т.к. он отсутствует " +
                    "в списке)", renterId);
            throw new EntityNotFoundException(String.format("Невозможно найти бронь у пользователя с id = %d, т.к. " +
                    "он отсутствует в списке", renterId));
        }
    }

    @Override
    public List<Booking> findAllByOwnerId(Long ownerId, State state, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (userRepository.findById(ownerId).isPresent()) {
            LocalDateTime now = LocalDateTime.now();
            switch (state) {
                case ALL:
                    return bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(ownerId, pageable).toList();
                case FUTURE:
                    return bookingRepository.findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(ownerId, now, pageable)
                            .toList();
                case PAST:
                    return bookingRepository.findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(ownerId, now, pageable)
                            .toList();
                case CURRENT:
                    return bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId,
                            now, now, pageable).toList();
                case WAITING:
                    return bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING,
                            pageable).toList();
                case REJECTED:
                    return bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED,
                            pageable).toList();
                default:
                    throw new IllegalArgumentException("Unknown state: " + state);
            }
        } else {
            log.info("EntityNotFoundException (Невозможно найти бронь у пользователя с id = {}, т.к. он отсутствует " +
                    "в списке)", ownerId);
            throw new EntityNotFoundException(String.format("Невозможно найти бронь у пользователя с id = %d, т.к. " +
                    "он отсутствует в списке", ownerId));
        }
    }
}
