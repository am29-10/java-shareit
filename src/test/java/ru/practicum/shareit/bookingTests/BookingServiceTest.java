package ru.practicum.shareit.bookingTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceTest {

    @Autowired
    BookingService bookingService;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    ItemRequestRepository requestRepository;

    User user;
    User user2;
    User user3;
    Item item;
    Booking booking;

    ItemRequest request;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();
        user3 = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@mail.ru")
                .build();
        request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
        item = Item.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .available(true)
                .owner(user)
                .itemRequest(request)
                .build();
        booking = Booking.builder()
                .id(1L)
                .booker(user2)
                .status(Status.WAITING)
                .item(item)
                .start(LocalDateTime.of(2025, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(2025, 2, 1, 1, 1, 1))
                .build();
    }

    @Test
    void create() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingRepository.save(any()))
                .thenReturn(booking);
        Booking booking1 = bookingService.create(booking, user2.getId());

        assertEquals(booking1.getBooker(), user);

        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(4)).findById(any());
        verify(bookingRepository, times(1)).save(any());

    }

    @Test
    void createFailWithItemRepository() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.create(booking, user2.getId()));

        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    void createFailWithItemRepositoryIsFalseAvailable() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        item.setAvailable(false);
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () -> bookingService.create(booking, user2.getId()));

        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(3)).findById(any());
    }

    @Test
    void createFailWithItemRepositoryWishOwner() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class, () -> bookingService.create(booking, user.getId()));

        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(4)).findById(any());
    }

    @Test
    void setStatus() {
        Mockito
                .when(bookingRepository.save(any()))
                .thenReturn(booking);
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        bookingService.setStatus(booking.getId(), user.getId(), true);

        assertEquals(booking.getStatus(), Status.APPROVED);

        verify(bookingRepository, times(5)).findById(any());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void setStatusWithFalseApproved() {
        Mockito
                .when(bookingRepository.save(any()))
                .thenReturn(booking);
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        bookingService.setStatus(booking.getId(), user.getId(), false);

        assertEquals(booking.getStatus(), Status.REJECTED);

        verify(bookingRepository, times(5)).findById(any());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void setStatusFailOwner() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class, () -> bookingService.setStatus(booking.getId(), user3.getId(),
                true));

        verify(bookingRepository, times(2)).findById(any());
    }

    @Test
    void setStatusFailWithoutBooking() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.setStatus(booking.getId(), user3.getId(),
                true));

        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    void setStatusWithApprovedStatus() {
        booking.setStatus(Status.APPROVED);
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class, () -> bookingService.setStatus(booking.getId(), user.getId(),
                true));

        verify(bookingRepository, times(3)).findById(any());
    }

    @Test
    void setStatusWithRejectedStatus() {
        booking.setStatus(Status.REJECTED);
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class, () -> bookingService.setStatus(booking.getId(), user.getId(),
                true));

        verify(bookingRepository, times(3)).findById(any());
    }

    @Test
    void setStatusWithCanceledStatus() {
        booking.setStatus(Status.CANCELED);
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class, () -> bookingService.setStatus(booking.getId(), user.getId(),
                true));

        verify(bookingRepository, times(3)).findById(any());
    }

    @Test
    void setStatusWithUnsupportedStatus() {
        booking.setStatus(Status.UNSUPPORTED_STATUS);
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class, () -> bookingService.setStatus(booking.getId(), user.getId(),
                true));

        verify(bookingRepository, times(3)).findById(any());
    }

    @Test
    void get() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Booking booking1 = bookingService.get(booking.getId(), user.getId());

        assertEquals(booking1, booking);

        verify(bookingRepository, times(4)).findById(any());
    }

    @Test
    void getFailWithEmptyBooking() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.get(booking.getId(), user.getId()));

        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    void getFailWithBookerOrOwner() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class, () -> bookingService.get(booking.getId(), user3.getId()));

        verify(bookingRepository, times(3)).findById(any());
    }

    @Test
    void findAllByRenterId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito
                .when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito
                .when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));
        Mockito
                .when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(),
                        any(), any()))
                .thenReturn(new PageImpl<>(List.of()));
        Mockito
                .when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<Booking> bookingsAll = bookingService.findAllByRenterId(user.getId(), State.ALL, 0, 10);
        List<Booking> bookingsFuture = bookingService.findAllByRenterId(user.getId(), State.FUTURE, 0, 10);
        List<Booking> bookingsPast = bookingService.findAllByRenterId(user.getId(), State.PAST, 0, 10);
        List<Booking> bookingsCurrent = bookingService.findAllByRenterId(user.getId(), State.CURRENT,
                0, 10);
        List<Booking> bookingsWaiting = bookingService.findAllByRenterId(user.getId(), State.WAITING,
                0, 10);

        assertEquals(bookingsAll.size(), 1);
        assertEquals(bookingsFuture.size(), 1);
        assertEquals(bookingsPast.size(), 0);
        assertEquals(bookingsCurrent.size(), 0);
        assertEquals(bookingsWaiting.size(), 1);
        assertThrows(IllegalArgumentException.class, () -> bookingService.findAllByRenterId(user.getId(),
                State.UNSUPPORTED_STATUS, 0, 10));

        verify(userRepository, times(6)).findById(any());
        verify(bookingRepository, times(1)).findAllByBookerIdOrderByStartDesc(anyLong(), any());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStartAfterOrderByStartDesc(
                anyLong(), any(), any());
        verify(bookingRepository, times(1)).findAllByBookerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(), any());
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());
    }

    @Test
    void findAllByRenterIdWithStateRejected() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));
        List<Booking> bookingsRejected = bookingService.findAllByRenterId(user.getId(), State.REJECTED,
                0, 10);

        assertEquals(bookingsRejected.size(), 0);

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());

    }

    @Test
    void findAllByRenterIdFail() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.findAllByRenterId(user.getId(), State.WAITING,
                0, 10));

        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void findAllByRenterIdFailNegativeFrom() {
        assertThrows(IllegalArgumentException.class, () -> bookingService.findAllByRenterId(user.getId(), State.WAITING,
                -1, 10));
    }

    @Test
    void findAllByOwnerId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito
                .when(bookingRepository.findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito
                .when(bookingRepository.findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));
        Mockito
                .when(bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(),
                        any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));
        Mockito
                .when(bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(anyLong(), any(),
                        any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<Booking> bookingsAll = bookingService.findAllByOwnerId(user.getId(), State.ALL, 0, 10);
        List<Booking> bookingsFuture = bookingService.findAllByOwnerId(user.getId(), State.FUTURE, 0, 10);
        List<Booking> bookingsPast = bookingService.findAllByOwnerId(user.getId(), State.PAST, 0, 10);
        List<Booking> bookingsCurrent = bookingService.findAllByOwnerId(user.getId(), State.CURRENT,
                0, 10);
        List<Booking> bookingsWaiting = bookingService.findAllByOwnerId(user.getId(), State.WAITING,
                0, 10);

        assertEquals(bookingsAll.size(), 1);
        assertEquals(bookingsFuture.size(), 1);
        assertEquals(bookingsPast.size(), 0);
        assertEquals(bookingsCurrent.size(), 0);
        assertEquals(bookingsWaiting.size(), 1);
        assertThrows(IllegalArgumentException.class, () -> bookingService.findAllByOwnerId(user.getId(),
                State.UNSUPPORTED_STATUS, 0, 10));

        verify(userRepository, times(6)).findById(any());
        verify(bookingRepository, times(1)).findAllByItem_OwnerIdOrderByStartDesc(anyLong(), any());
        verify(bookingRepository, times(1)).findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(
                anyLong(), any(), any());
        verify(bookingRepository, times(1)).findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(), any());
        verify(bookingRepository, times(1))
                .findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
        verify(bookingRepository, times(1)).findAllByItem_OwnerIdAndStatusOrderByStartDesc(
                anyLong(), any(), any());
    }

    @Test
    void findAllByOwnerIdWithStateRejected() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(anyLong(), any(),
                        any()))
                .thenReturn(new PageImpl<>(List.of()));
        List<Booking> bookingsRejected = bookingService.findAllByOwnerId(user.getId(), State.REJECTED,
                0, 10);

        assertEquals(bookingsRejected.size(), 0);

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findAllByItem_OwnerIdAndStatusOrderByStartDesc(
                anyLong(), any(), any());
    }

    @Test
    void findAllByOwnerIdFail() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.findAllByOwnerId(user.getId(), State.WAITING,
                0, 10));

        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void findAllByOwnerIdFailWithNegativeFrom() {
        assertThrows(IllegalArgumentException.class, () -> bookingService.findAllByOwnerId(user.getId(), State.WAITING,
                -1, 10));

    }

    @Test
    void validateStart() {
        booking.setStart(LocalDateTime.of(2020, 1, 1, 1, 1, 1, 1));

        assertThrows(IllegalArgumentException.class, () -> bookingService.create(booking, user.getId()));
    }

    @Test
    void validateEnd() {
        booking.setEnd(LocalDateTime.of(2020, 1, 1, 1, 1, 1, 1));

        assertThrows(IllegalArgumentException.class, () -> bookingService.create(booking, user.getId()));
    }
}