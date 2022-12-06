package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank(message = "Комментарий не может быть пустым")
    @NotNull(message = "Текст не может быть равен null")
    private String text;
    @Positive
    private Long authorId;
    private String authorName;
    private LocalDateTime created;
}
