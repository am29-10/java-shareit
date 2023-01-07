package ru.practicum.shareit.bookingTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;

    @Autowired
    private JacksonTester<BookingItemDto> jsonBookingItemDto;

    @Test
    void toBookingDto() throws IOException {
        BookingDto bookingDto = BookingDto
                .builder()
                .id(1L)
                .start(LocalDateTime.of(2022,1,1,1,1,1,1))
                .end(LocalDateTime.of(2023,1,1,1,1,1,1))
                .itemId(1L)
                .bookerId(1L)
                .status(Status.WAITING)
                .build();
        JsonContent<BookingDto> result = jsonBookingDto.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(LocalDateTime.of(2022,
                1,1,1,1,1,1).toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(LocalDateTime.of(2023,
                1,1,1,1,1,1).toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(Status.WAITING.toString());
    }

    @Test
    void toBookingItemDto() throws IOException {
        BookingItemDto bookingItemDto = BookingItemDto.builder()
                .id(1L)
                .bookerId(1L)
                .build();
        JsonContent<BookingItemDto> result = jsonBookingItemDto.write(bookingItemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);

    }
}
