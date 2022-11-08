package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {

    @Positive
    private Long id;
    @NotBlank(message = "Название предмета не может быть пустым.")
    private String name;
    @NotBlank(message = "Описание предмета не может быть пустым.")
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest itemRequest;
}
