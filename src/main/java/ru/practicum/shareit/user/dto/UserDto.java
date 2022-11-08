package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    @Positive
    private Long id;
    @NotBlank(message = "Имя пользователя не может быть пустым.")
    private String name;
    @NotBlank(message = "email не может быть пустым.")
    @Email(message = "указанный email не соответствует правильной структуре 'email'.")
    private String email;
}
