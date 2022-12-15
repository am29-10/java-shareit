package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Название предмета не может быть пустым.")
    private String name;
    @NotBlank(message = "Описание предмета не может быть пустым.")
    private String description;
    @NotNull(message = "available не может быть null")
    private Boolean available;
    @Positive
    private Long requestId;
}
