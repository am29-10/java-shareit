package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private long id;
    @NotNull(message = "Описание не может быть null")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}
