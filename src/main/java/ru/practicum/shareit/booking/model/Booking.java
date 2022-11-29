package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotNull(message = "Дата начала бронирования не может быть пустой")
    @Column(name = "start_date")
    private LocalDateTime start;
    @NotNull(message = "Дата завершения бронирования не может быть пустой")
    @Column(name = "end_date")
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker;
    @NotNull(message = "Статус бронирования не может быть пустым")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;


}
