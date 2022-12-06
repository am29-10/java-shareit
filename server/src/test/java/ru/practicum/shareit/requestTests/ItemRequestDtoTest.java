package ru.practicum.shareit.requestTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.dto.ItemRequestWithoutAnswersDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestWithAnswersDto> jsonItemRequestWithAnswersDto;

    @Autowired
    private JacksonTester<ItemRequestWithoutAnswersDto> jsonItemRequestWithoutAnswersDto;

    User user;
    Item item;
    ItemRequest request;

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
                .name("item")
                .description("description")
                .available(true)
                .owner(user)
                .itemRequest(request)
                .build();
    }

    @Test
    void toItemRequestWithAnswersDto() throws IOException {
        ItemRequestWithAnswersDto itemRequestWithAnswersDto = ItemRequestWithAnswersDto.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1))
                .items(List.of(ItemMapper.toItemDto(item)))
                .build();
        JsonContent<ItemRequestWithAnswersDto> result = jsonItemRequestWithAnswersDto.write(itemRequestWithAnswersDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requestor.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(LocalDateTime.of(2025,
                1, 1, 1, 1, 1, 1).toString());
        assertThat(result).extractingJsonPathArrayValue("$.items").extracting("id").contains(1);
    }

    @Test
    void toItemRequestWithoutAnswersDto() throws IOException {
        ItemRequestWithoutAnswersDto itemRequestWithoutAnswersDto = ItemRequestWithoutAnswersDto.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1))
                .build();
        JsonContent<ItemRequestWithoutAnswersDto> result = jsonItemRequestWithoutAnswersDto
                .write(itemRequestWithoutAnswersDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requestor.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(LocalDateTime.of(2025,
                1, 1, 1, 1, 1, 1).toString());
    }
}
