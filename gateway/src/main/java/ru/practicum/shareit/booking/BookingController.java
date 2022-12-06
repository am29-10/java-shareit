package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@RequestParam(name = "from", defaultValue = "0") Integer from,
			@RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Получен запрос GET /bookings?state={}", state);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Получен запрос POST /items");
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId) {
		log.info("Получен запрос GET /booking/{}", bookingId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
											  @RequestParam(name = "state", defaultValue = "all") String stateParam,
											  @RequestParam(defaultValue = "0") Integer from,
											  @RequestParam(defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Получен запрос GET /owner");
		return bookingClient.getBookingsByOwner(userId, state, from, size);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> setStatus(@RequestHeader("X-Sharer-User-Id") long userId,
											@Positive @PathVariable long bookingId,
											 @RequestParam boolean approved) {
		log.info("Получен запрос PATCH /bookings/{}={}", bookingId, approved);
		return bookingClient.setStatus(userId, bookingId, approved);
	}

}
