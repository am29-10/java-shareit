package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(message = "Имя не может быть пустым")
    @NotNull(message = "Имя не может быть пустым")
    private String name;
    @NotBlank(message = "Email не может быть пустым")
    @NotNull(message = "Email не может быть null")
    @Email(message = "Email должен соответствовать требованиям 'email'")
    private String email;
}
