package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {

    private Long id;
    @NotNull(message = "Дата начала бронирования не может быть пустой")
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull(message = "Дата завершения бронирования не может быть пустой")
    @FutureOrPresent
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private Status status;

}
