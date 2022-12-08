package ru.practicum.shareit.requestTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
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
class ItemRequestServiceTest {

    @Autowired
    ItemRequestService requestService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    ItemRequestRepository requestRepository;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    BookingRepository bookingRepository;

    User user;
    User user2;
    Item item;
    Item itemUpdate;
    Comment comment;
    Booking booking;
    ItemRequest request;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@mail.ru")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();
        request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
        item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(user)
                .itemRequest(request)
                .build();
        itemUpdate = Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();
        comment = Comment.builder()
                .id(1L)
                .author(user)
                .item(item)
                .text("text")
                .build();
        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
    }

    @Test
    void create() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(requestRepository.save(any()))
                .thenReturn(request);
        ItemRequest request1 = requestService.create(request, user.getId());

        assertEquals(request1, request);

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).save(any());
    }

    @Test
    void getAllByRequestorId() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(requestRepository.findAllByRequestor_IdOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(request)));
        request.setItems(List.of(item));
        List<ItemRequestWithAnswersDto> requests = requestService.getAllByRequestorId(user.getId(), 0, 10);

        assertEquals(requests.size(), 1);

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findAllByRequestor_IdOrderByCreatedDesc(anyLong(),
                any());
    }

    @Test
    void getAllByRequestorIdFail() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.getAllByRequestorId(user.getId(), 0, 10));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getById() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        request.setItems(List.of(item));
        ItemRequestWithAnswersDto request1 = requestService.getById(user.getId(), request.getId());

        assertEquals(request1.getId(), request.getId());

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void getByIdFailUserEmpty() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        assertThrows(EntityNotFoundException.class, () -> requestService.getById(user.getId(), request.getId()));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getByIdFailRequest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.getById(user.getId(), request.getId()));

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAll() {
        Mockito
                .when(requestRepository.findAll((Pageable) any()))
                .thenReturn(new PageImpl<>(List.of(request)));
        request.setItems(List.of(item));
        List<ItemRequestWithAnswersDto> requests = requestService.getAll(user2.getId(), 0, 10);

        assertEquals(requests.size(), 1);

        verify(requestRepository, times(1)).findAll((Pageable) any());
    }
}