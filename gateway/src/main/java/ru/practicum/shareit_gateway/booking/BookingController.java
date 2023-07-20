package ru.practicum.shareit_gateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import javax.validation.Valid;
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
import ru.practicum.shareit_gateway.util.EndpointObjectsValidator;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> save(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
									   @RequestBody @Valid BookingRestCommand bookingRestCommand) {
		log.debug("Gateway received ru.practicum.shareit.request to create a ru.practicum.shareit.booking {}, userId={}", bookingRestCommand, userId);
		EndpointObjectsValidator.checkUserIdForNullValue(userId, "сохранение");
		return bookingClient.save(userId, bookingRestCommand);
	}

	@PatchMapping("{booking_id}")
	public ResponseEntity<Object> changeStatus(
			@PositiveOrZero @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
			@Positive @PathVariable(name = "booking_id") long bookingId,
			@RequestParam(name = "approved", required = false) Boolean isApproved) {
		log.debug("Gateway received ru.practicum.shareit.request to change status of ru.practicum.shareit.booking with id{}, from ru.practicum.shareit.user with id{}, approved={}",
				bookingId, userId, isApproved);
		EndpointObjectsValidator.checkUserIdForNullValue(userId, "изменение статуса");
		if (isApproved == null) {
			throw new BadRequestParameterException("В запросе на изменение статуса бронирования не указано " +
					"подтверждено оно или нет");
		}
		return bookingClient.changeStatus(userId, bookingId, isApproved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllWithStateForOwnersItems(
			@PositiveOrZero @RequestHeader(value = "X-Sharer-User-Id") long ownerId,
			@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
			@RequestParam(name = "from", defaultValue = "0") int from,
			@RequestParam(name = "size", defaultValue = "10") int size) {
		log.debug("Gateway received ru.practicum.shareit.request to get all ru.practicum.shareit.booking for items of owner with state {}, ownerId={}, from={}, " +
						"size={}", stateParam, ownerId, from, size);
		EndpointObjectsValidator.checkUserIdForNullValue(ownerId, "получение всех бронирований хозяином вещи");
		EndpointObjectsValidator.checkPaginationParameters(from, size);
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateParam));
		return bookingClient.getBookingsOfOwnersItems(ownerId, state, from, size);
	}

	@GetMapping
	public ResponseEntity<Object> getAllWithStateForBooker(
			@PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@RequestParam(name = "from", defaultValue = "0") Integer from,
			@RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.debug("Gateway received ru.practicum.shareit.request to get all ru.practicum.shareit.booking of ru.practicum.shareit.user with state {}, userId={}, from={}, size={}",
				stateParam, userId, from, size);
		EndpointObjectsValidator.checkUserIdForNullValue(userId,
				"получение всех бронирований, оформленных пользователем");
		EndpointObjectsValidator.checkPaginationParameters(from, size);
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateParam));
		return bookingClient.getBookingsOfUser(userId, state, from, size);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
			@Positive @PathVariable Long bookingId) {
		log.debug("Gateway received ru.practicum.shareit.request to get ru.practicum.shareit.booking with id{}, userId={}", bookingId, userId);
		EndpointObjectsValidator.checkUserIdForNullValue(userId, "получение по идентификатору");
		return bookingClient.getById(userId, bookingId);
	}

}