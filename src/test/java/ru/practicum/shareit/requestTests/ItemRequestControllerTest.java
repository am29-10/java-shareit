package ru.practicum.shareit.requestTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService requestService;

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

    @Test
    void create() throws Exception {
        Mockito
                .when(requestService.create(any(), anyLong()))
                .thenReturn(request);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByRequestorId() throws Exception {
        request.setItems(List.of(item));
        List<ItemRequestWithAnswersDto> requests = List.of(ItemRequestMapper.toItemRequestWithAnswersDto(request));
        Mockito
                .when(requestService.getAllByRequestorId(anyLong(), any(), any()))
                .thenReturn(requests);

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAll() throws Exception {
        request.setItems(List.of(item));
        List<ItemRequestWithAnswersDto> requests = List.of(ItemRequestMapper.toItemRequestWithAnswersDto(request));
        Mockito
                .when(requestService.getAll(anyLong(), any(), any()))
                .thenReturn(requests);

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllFail() throws Exception {
        request.setItems(List.of(item));
        List<ItemRequestWithAnswersDto> requests = List.of(ItemRequestMapper.toItemRequestWithAnswersDto(request));
        Mockito
                .when(requestService.getAll(anyLong(), any(), any()))
                .thenReturn(requests);

        mvc.perform(get("/requests/all?from=-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById() throws Exception {
        request.setItems(List.of(item));
        ItemRequestWithAnswersDto request1 = ItemRequestMapper.toItemRequestWithAnswersDto(request);
        Mockito
                .when(requestService.getById(anyLong(), anyLong()))
                .thenReturn(request1);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}