package ru.practicum.shareit.itemTests;

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
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceTest {
    @Autowired
    ItemService itemService;

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
                .when(itemRepository.save(any()))
                .thenReturn(Optional.of(item).get());
        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        ItemDto itemDto = itemService.create(item, user.getId());

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getOwner().getId(), user.getId());
        assertEquals(itemDto.getAvailable(), item.getAvailable());

        verify(itemRepository, times(1)).save(any());
        verify(requestRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void readAll() {
        List<Item> items = List.of(item);
        Mockito
                .when(itemRepository.findAll((Pageable) any()))
                .thenReturn(new PageImpl<>(items));
        List<Item> items2 = itemService.readAll(0, 10);

        assertEquals(items2.size(), 1);

        verify(itemRepository, times(1)).findAll((Pageable) any());
    }

    @Test
    void readAllByUserId() {
        List<Item> items = List.of(item);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findAllByOwner(any(), any()))
                .thenReturn(new PageImpl<>(items));
        List<ItemBookingDto> items2 = itemService.readAllByUserId(1L, 1, 10);

        assertEquals(items2.size(), 1);

        verify(itemRepository, times(1)).findAllByOwner(any(), any());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void update() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(Optional.of(item).get());
        Item item = itemService.update(1L, itemUpdate, user.getId());

        assertEquals(item.getId(), item.getId());
        assertEquals(item.getName(), item.getName());
        assertEquals(item.getDescription(), item.getDescription());
        assertEquals(item.getOwner().getId(), user.getId());
        assertEquals(item.getAvailable(), item.getAvailable());

        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void updateFailWithoutItem() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.update(1L, itemUpdate, user.getId()));

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateFailWithoutOwner() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class, () -> itemService.update(1L, itemUpdate, user2.getId()));

        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
    }


    @Test
    void getItemById() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Item item = itemService.getItemById(1L);

        assertEquals(item.getName(), "item");

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getItemByUserId() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(commentRepository.findAllByItem(any()))
                .thenReturn(List.of(comment));
        ItemBookingDto item = itemService.getItemByUserId(1L, 1L);

        assertEquals(item.getName(), "item");

        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findAllByItem(any());
    }

    @Test
    void getItemByUserIdFailWithoutItem() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(commentRepository.findAllByItem(any()))
                .thenReturn(List.of(comment));

        assertThrows(EntityNotFoundException.class, () -> itemService.getItemByUserId(1L, 1L));

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getItemByUserIdWithAnotherOwner() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        Mockito
                .when(commentRepository.findAllByItem(any()))
                .thenReturn(List.of(comment));
        ItemBookingDto item = itemService.getItemByUserId(1L, 2L);

        assertNotNull(item.getComments());

        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findAllByItem(any());
    }

    @Test
    void delete() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());


        assertThrows(EntityNotFoundException.class, () -> itemService.delete(1L, 1L));

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void findItemsByText() {
        Mockito
                .when(itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(item)));
        String text = "item";
        List<ItemDto> items = itemService.findItemsByText(text, 1, 10);

        assertEquals(items.size(), 1);

        verify(itemRepository, times(1))
                .findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(any(), any(), any());
    }

    @Test
    void findItemsByTextIsEmpty() {
        String text = "";
        List<ItemDto> items = itemService.findItemsByText(text, 1, 10);

        assertEquals(items.size(), 0);

    }

    @Test
    void createComment() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingRepository.findAllByItemIdAndAndBooker_IdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(List.of(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(commentRepository.save(any()))
                .thenReturn(Optional.of(comment).get());
        CommentDto createComment = itemService.createComment(1L, 1L, comment);

        assertEquals(createComment.getText(), "text");

        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByItemIdAndAndBooker_IdAndEndBefore(anyLong(),
                anyLong(), any());
        verify(userRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void createCommentFailWithoutItem() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Mockito
                .when(bookingRepository.findAllByItemIdAndAndBooker_IdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(List.of(booking));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(commentRepository.save(any()))
                .thenReturn(Optional.of(comment).get());

        assertThrows(EntityNotFoundException.class, () -> itemService.createComment(1L, 1L, comment));

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void createCommentFailWithoutBooking() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingRepository.findAllByItemIdAndAndBooker_IdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(List.of());
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(commentRepository.save(any()))
                .thenReturn(Optional.of(comment).get());

        assertThrows(IllegalArgumentException.class, () -> itemService.createComment(1L, 1L, comment));

        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByItemIdAndAndBooker_IdAndEndBefore(anyLong(),
                anyLong(), any());
    }

    @Test
    void addLastAndNextBooking() {
        booking.setStart(LocalDateTime.of(2019, 1, 1, 1, 1, 1, 1));
        booking.setEnd(LocalDateTime.of(2020, 1, 1, 1, 1, 1, 1));
        Booking booking2 = Booking.builder()
                .id(2L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(2026, 1, 1, 1, 1, 1, 1))
                .status(Status.WAITING)
                .build();
        Mockito
                .when(bookingRepository.findByItemAndEndBeforeOrderByEndDesc(any(), any()))
                .thenReturn(booking);
        Mockito
                .when(bookingRepository.findByItemAndStartAfterOrderByStart(any(), any()))
                .thenReturn(booking);

        assertNotNull(booking.getEnd());
        assertNotNull(booking2.getStart());

    }
}