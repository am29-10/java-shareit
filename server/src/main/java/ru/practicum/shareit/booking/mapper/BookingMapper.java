package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto) {
        return Booking
                .builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(Item.builder()
                        .id(bookingDto.getItemId())
                        .build())
                .booker(User.builder()
                        .id(bookingDto.getBookerId())
                        .build())
                .build();
    }

    public static BookingItemDto toShortDto(Booking booking) {
        return new BookingItemDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }

}
