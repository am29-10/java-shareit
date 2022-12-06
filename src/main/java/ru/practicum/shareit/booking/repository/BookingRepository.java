package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start,
                                                                             LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long itemOwnerId, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long itemOwnerId, LocalDateTime start,
                                                                                 LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(Long itemOwnerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(Long itemOwnerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(Long itemOwnerId, Status status, Pageable pageable);

    List<Booking> findAllByItemIdAndAndBooker_IdAndEndBefore(Long itemId, Long userId, LocalDateTime
            time);

    Booking findByItemAndEndBeforeOrderByEndDesc(Item item, LocalDateTime today);

    Booking findByItemAndStartAfterOrderByStart(Item item, LocalDateTime today);

    List<Booking> findAllByStatusOrderByStartDesc(Status status);

    List<Booking> findAllByItem_IdOrderByStartDesc(Long itemId);

}
