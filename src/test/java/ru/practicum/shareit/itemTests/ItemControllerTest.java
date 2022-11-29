package ru.practicum.shareit.itemTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {


    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;

    User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@mail.ru")
            .build();
    ItemRequest request = ItemRequest.builder()
            .id(1L)
            .description("description")
            .requestor(user)
            .created(LocalDateTime.now())
            .build();
    Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .owner(user)
            .itemRequest(request)
            .build();
    Item itemUpdate = Item.builder()
            .name("item")
            .description("description")
            .available(true)
            .build();
    Comment comment = Comment.builder()
            .id(1L)
            .author(user)
            .item(item)
            .text("text")
            .build();

    @Test
    void getAll() throws Exception {
        ItemBookingDto itemBookingDto = ItemMapper.toItemWishBookingAndCommentDto(item, null, null,
                List.of(CommentMapper.toCommentDto(comment)));
        Mockito
                .when(itemService.readAllByUserId(anyLong(), any()))
                .thenReturn(List.of(itemBookingDto));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllFail() throws Exception {
        ItemBookingDto itemBookingDto = ItemMapper.toItemWishBookingAndCommentDto(item, null, null,
                List.of(CommentMapper.toCommentDto(comment)));
        Mockito
                .when(itemService.readAllByUserId(anyLong(), any()))
                .thenReturn(List.of(itemBookingDto));

        mvc.perform(get("/items?from=-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItem() throws Exception {
        ItemBookingDto itemBookingDto = ItemMapper.toItemWishBookingAndCommentDto(item, null, null,
                List.of(CommentMapper.toCommentDto(comment)));
        Mockito
                .when(itemService.getItemByUserId(anyLong(), anyLong()))
                .thenReturn(itemBookingDto);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void create() throws Exception {
        ItemDto itemDto = ItemMapper.toItemDto(itemUpdate);
        Mockito
                .when(itemService.create(any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void update() throws Exception {
        Mockito
                .when(itemService.update(anyLong(), any(), anyLong()))
                .thenReturn(item);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteItem() throws Exception {
        Mockito
                .when(itemService.getItemById(anyLong()))
                .thenReturn(null);

        mvc.perform(delete("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searching() throws Exception {
        ItemDto itemDto = ItemMapper.toItemDto(itemUpdate);
        List<ItemDto> items = List.of(itemDto);
        Mockito
                .when(itemService.findItemsByText(any(), any()))
                .thenReturn(items);

        mvc.perform(get("/items/search?text='item'")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        Mockito
                .when(itemService.createComment(anyLong(), any(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}