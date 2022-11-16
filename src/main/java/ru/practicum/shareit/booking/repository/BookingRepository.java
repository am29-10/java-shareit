package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long id);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime time1,
                                                                             LocalDateTime time2);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime time);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long id, LocalDateTime time);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long id, Status status);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long id);

    List<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime time1,
                                                                                 LocalDateTime time2);

    List<Booking> findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime time);

    List<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(Long id, LocalDateTime time);

    List<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(Long id, Status status);

    Booking findFirstByItem_Owner_IdAndAndItem_IdOrderByStart(Long userId, Long itemId);

    Booking findFirstByItem_OwnerIdAndIdOrderByStartDesc(Long userId, Long itemId);

    List<Booking> findAllByItemIdAndAndBooker_IdAndEndBefore(Long itemId, Long userId, LocalDateTime
            time);


}
