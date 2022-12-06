package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestWithoutAnswersDto {

    private Long id;
    @NotBlank(message = "Описание не может быть пустым.")
    private String description;
    private User requestor;
    private LocalDateTime created;
}
