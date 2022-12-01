package ru.practicum.shareit.bookingTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;
    User user;
    Booking booking;
    ItemRequest request;
    Item item;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@mail.ru")
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
                .booker(user)
                .status(Status.WAITING)
                .item(item)
                .start(LocalDateTime.of(2025, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(2025, 2, 1, 1, 1, 1))
                .build();
    }

    @Test
    void getAll() throws Exception {
        Mockito
                .when(bookingService.findAllByRenterId(user.getId(), State.ALL, any(), any()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).findAllByRenterId(anyLong(), any(), any(), any());
    }

    @Test
    void getAllFail() throws Exception {
        Mockito
                .when(bookingService.findAllByRenterId(user.getId(), State.ALL, any(), any()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings?from=-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void getAllByOwnerId() throws Exception {
        Mockito
                .when(bookingService.findAllByOwnerId(user.getId(), State.ALL, 0, 10))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).findAllByOwnerId(anyLong(), any(), any(), any());
    }

    @Test
    void getAllByOwnerIdFail() throws Exception {
        Mockito
                .when(bookingService.findAllByOwnerId(user.getId(), State.ALL, 0, 10))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner?from=-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBooking() throws Exception {
        Mockito
                .when(bookingService.get(booking.getId(), user.getId()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).get(anyLong(), anyLong());
    }

    @Test
    void create() throws Exception {
        Booking bookingNew = Booking.builder()
                .status(Status.WAITING)
                .item(item)
                .start(LocalDateTime.of(2025, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(2025, 2, 1, 1, 1, 1))
                .build();
        Mockito
                .when(bookingService.create(bookingNew, user.getId()))
                .thenReturn(bookingNew);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingNew))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).create(any(), anyLong());
    }

    @Test
    void setStatus() throws Exception {
        Mockito
                .when(bookingService.setStatus(booking.getId(), user.getId(), true))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}