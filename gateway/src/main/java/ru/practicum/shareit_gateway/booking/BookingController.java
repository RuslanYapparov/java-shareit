package ru.practicum.shareit_gateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit_gateway.booking.dto.BookingRestCommand;
import ru.practicum.shareit_gateway.booking.dto.BookingState;
import ru.practicum.shareit_gateway.exception.BadRequestParameterException;
import ru.practicum.shareit_gateway.exception.UnsupportedStateException;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> save(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
									   @Valid @RequestBody BookingRestCommand bookingRestCommand) {
		log.debug("Gateway received request to create a booking {}, userId={}", bookingRestCommand, userId);
		return bookingClient.save(userId, bookingRestCommand);
	}

	@PatchMapping("{booking_id}")
	public ResponseEntity<Object> changeStatus(
			@Positive @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
			@Positive @PathVariable(name = "booking_id") long bookingId,
			@RequestParam(name = "approved", required = false) Boolean isApproved) {
		log.debug("Gateway received request to change status of booking with id{}, from user with id{}, approved={}",
				bookingId, userId, isApproved);
		if (isApproved == null) {
			throw new BadRequestParameterException("В запросе на изменение статуса бронирования не указано " +
					"подтверждено оно или нет");
		}
		return bookingClient.changeStatus(userId, bookingId, isApproved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllWithStateForOwnersItems(
			@Positive @RequestHeader(value = "X-Sharer-User-Id") long ownerId,
			@NotBlank @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
			@Positive @RequestParam(name = "size", defaultValue = "10") int size) {
		log.debug("Gateway received request to get all booking for items of owner with state {}, ownerId={}, from={}, " +
						"size={}", stateParam, ownerId, from, size);
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateParam));
		return bookingClient.getBookingsOfOwnersItems(ownerId, state, from, size);
	}

	@GetMapping
	public ResponseEntity<Object> getAllWithStateForBooker(
			@Positive @RequestHeader("X-Sharer-User-Id") long userId,
			@NotBlank @RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.debug("Gateway received request to get all booking of ru.practicum.shareit.user with state {}, userId={}, from={}, size={}",
				stateParam, userId, from, size);
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateParam));
		return bookingClient.getBookingsOfUser(userId, state, from, size);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
			@Positive @PathVariable Long bookingId) {
		log.debug("Gateway received request to get booking with id{}, userId={}", bookingId, userId);
		return bookingClient.getById(userId, bookingId);
	}

}