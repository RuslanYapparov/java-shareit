package ru.practicum.shareit_server.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.practicum.shareit_server.booking.dao.BookingEntity;
import ru.practicum.shareit_server.booking.dao.BookingRepository;
import ru.practicum.shareit_server.booking.dto.BookingRestCommand;
import ru.practicum.shareit_server.booking.dto.BookingRestView;
import ru.practicum.shareit_server.common.CrudServiceImpl;
import ru.practicum.shareit_server.exception.InternalLogicException;
import ru.practicum.shareit_server.item.dao.ItemEntity;
import ru.practicum.shareit_server.item.dao.ItemRepository;
import ru.practicum.shareit_server.user.dao.UserRepository;
import ru.practicum.shareit_server.exception.BadRequestBodyException;
import ru.practicum.shareit_server.exception.BadRequestParameterException;
import ru.practicum.shareit_server.exception.ObjectNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl extends CrudServiceImpl<BookingEntity, Booking, BookingRestCommand, BookingRestView>
        implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    Sort sortByBookingStart = Sort.by(Sort.Direction.DESC, "start");

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository,
                              BookingMapper bookingMapper) {
        this.entityRepository = bookingRepository;
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.objectMapper = bookingMapper;
        this.type = "booking";
    }

    @Override
    public BookingRestView save(long userId, BookingRestCommand bookingRestCommand) {
        ItemEntity savedItemEntity = getItemEntityForBookingIfCorrectParameters(userId, bookingRestCommand);
        bookingRestCommand = bookingRestCommand.toBuilder().build();
        Booking booking = objectMapper.fromRestCommand(bookingRestCommand);
        booking = booking.toBuilder()
                .bookerId(userId)
                .bookingStatus(BookingStatus.WAITING)
                .build();
        BookingEntity bookingEntity = objectMapper.toDbEntity(booking);
        bookingEntity.setItem(savedItemEntity);
        bookingEntity = bookingRepository.save(bookingEntity);
        List<BookingEntity> itemBookings = savedItemEntity.getItemBookings();
        if (itemBookings == null || itemBookings.isEmpty()) {
            itemBookings = new ArrayList<>(List.of(bookingEntity));
        } else {
            itemBookings.add(bookingEntity);
        }
        savedItemEntity.setItemBookings(itemBookings);
        itemRepository.save(savedItemEntity);
        booking = objectMapper.fromDbEntity(bookingEntity);
        log.info("Пользователь с идентификатором id{} забронировал вещь '{}' с идентификатором id{} в период с {} по {}." +
                        "Объекту '{}' присвоен идентификатор id{}", userId, savedItemEntity.getName(),
                booking.getItemId(), booking.getStart(), booking.getEnd(), type, booking.getId());
        return objectMapper.toRestView(booking);
    }

    @Override
    public BookingRestView changeBookingStatus(long userId, long bookingId, boolean isApproved) {
        checkExistingAndReturnUserShort(userId);
        BookingEntity bookingEntity = getBookingEntityIfCorrectBooker(userId, bookingId);
        long itemOwnerId = bookingEntity.getItem().getUserId();
        if (userId != itemOwnerId) {
            throw new ObjectNotFoundException(String.format("Операция изменения статуса бронирования с " +
                    "идентификатором id'%d' отклонена: только владелец вещи может изменять статус", bookingId));
        }
        String bookingStatus = bookingEntity.getStatus();
        if (isApproved && "APPROVED".equals(bookingStatus)) {
            throw new BadRequestParameterException(String.format("Пользователь с id%d уже устанавливал статус " +
                            "%s бронированию с id%d для своей вещи %s", userId, bookingStatus, bookingId,
                    bookingEntity.getItem().getName()));
        }
        bookingEntity.setStatus(isApproved ? BookingStatus.APPROVED.name() : BookingStatus.REJECTED.name());
        bookingEntity = bookingRepository.save(bookingEntity);
        Booking booking = objectMapper.fromDbEntity(bookingEntity);
        log.info("Пользователь с идентификатором id'{}' установил статус {} для бронирования принадлежащей ему " +
                "вещи {}. Идентификатор объекта {}: id'{}'", userId, booking.getBookingStatus(),
                booking.getItemName(), type, bookingId);
        return objectMapper.toRestView(booking);
    }

    @Override
    public Page<BookingRestView> getAllForBookerWithStateParameter(long userId, String state, int from, int size) {
        BookingState bookingState = BookingState.valueOf(state);
        checkExistingAndReturnUserShort(userId);
        Pageable page = PageRequest.of(from / size, size, sortByBookingStart);
        return getRestViewsForBookerWithIdAndState(bookingState, userId, page);
    }

    @Override
    public Page<BookingRestView> getAllForItemOwnerWithStateParameter(
            long itemOwnerId, String state, int from, int size) {
        BookingState bookingState = BookingState.valueOf(state);
        checkExistingAndReturnUserShort(itemOwnerId);
        Pageable page = PageRequest.of(from / size, size, sortByBookingStart);
        return getRestViewsForOwnerWithIdAndState(bookingState, itemOwnerId, page);
    }

    @Override
    public BookingRestView getById(long userId, long bookingId) {
        checkExistingAndReturnUserShort(userId);
        BookingEntity bookingEntity = getBookingEntityIfCorrectBooker(userId, bookingId);
        Booking booking = objectMapper.fromDbEntity(bookingEntity);
        log.info("Пользователь с идентификатором id{} запросил данные объекта '{}' с идентификатором id{}",
                userId, type, bookingId);
        return objectMapper.toRestView(booking);
    }

    private BookingEntity getBookingEntityIfCorrectBooker(long userId, long bookingId) {
        BookingEntity bookingEntity = findObjectEntityIfExists(bookingId);
        if (userId != bookingEntity.getUserId() && userId != bookingEntity.getItem().getUserId()) {
            throw new ObjectNotFoundException(String.format("В ходе выполнения операции над объектом '%s' с " +
                    "идентификатором id%d произошла ошибка: пользователь с id%d не является владельцем вещи и " +
                    "не бронировал ее у другого пользователя", type, bookingId, userId));
        }
        return bookingEntity;
    }

    private ItemEntity getItemEntityForBookingIfCorrectParameters(long userId, BookingRestCommand bookingRestCommand) {
        checkExistingAndReturnUserShort(userId);
        long itemId = bookingRestCommand.getItemId();
        ItemEntity itemEntity = itemRepository.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("При бронировании вещи с идентификатором id%d произошла " +
                    "ошибка: указан неверный идентификатор, вещь с этим идентификатором не была сохранена", itemId)));
        if (itemEntity.getUserId() == userId) {
            throw new ObjectNotFoundException(String.format("При бронировании вещи с идентификатором id%d произошла " +
                    "ошибка: в заголовке запроса передан идентификатор пользователя id%d, являющегося " +
                    "владельцем вещи", itemId, userId));
        }
        if (!itemEntity.isAvailable()) {
            throw new BadRequestBodyException(String.format("При бронировании вещи с идентификатором id%d произошла " +
                    "ошибка: данная вещь не доступна для бронирования", itemId));
        }
        LocalDateTime savingBookingStart = bookingRestCommand.getStart();
        LocalDateTime savingBookingEnd = bookingRestCommand.getEnd();
        if (savingBookingEnd.isBefore(savingBookingStart) || savingBookingStart.equals(savingBookingEnd)) {
            throw new BadRequestBodyException(String.format("При бронировании вещи с идентификатором id%d произошла " +
                    "ошибка: переданная дата конца бронирования '%s' раньше даты начала '%s' или равна ей",
                    itemId, savingBookingEnd, savingBookingStart));
        }
        if (itemEntity.getItemBookings() != null) {
            List<Booking> bookings = itemEntity.getItemBookings().stream()
                    .map(objectMapper::fromDbEntity)
                    .collect(Collectors.toList());
            if (!bookings.isEmpty()) {
                boolean isBookingTimeAlreadyTakenByAnotherBooking = bookings.stream()
                        .filter(booking -> BookingStatus.APPROVED.equals(booking.getBookingStatus()))
                        .filter(booking -> booking.getStart().isBefore(savingBookingEnd))
                        .anyMatch(booking -> booking.getEnd().isAfter(savingBookingStart));
                if (isBookingTimeAlreadyTakenByAnotherBooking) {
                    throw new BadRequestBodyException(String.format("При бронировании вещи с идентификатором id%d " +
                            "произошла ошибка: данная вещь уже была забронирована на этот период", itemId));
                }
            }
        }
        return itemEntity;
    }

    private Page<BookingRestView> getRestViewsForOwnerWithIdAndState(
            BookingState bookingState, long itemOwnerId, Pageable page) {
        Page<BookingEntity> requestedBookings;
        switch (bookingState) {
            case ALL:
                requestedBookings = bookingRepository.findAllByItemUserId(itemOwnerId, page);
                log.info("Запрошен список всех бронирований вещи владельца с идентификатором id{}", itemOwnerId);
                break;
            case CURRENT:
                requestedBookings = bookingRepository.findAllCurrentBookingsForItemOwnerById(itemOwnerId, page);
                log.info("Запрошен список всех текущих бронирований владельца с идентификатором id{}", itemOwnerId);
                break;
            case PAST:
                requestedBookings = bookingRepository.findAllPastBookingsForItemOwnerById(itemOwnerId, page);
                log.info("Запрошен список всех прошедших бронирований владельца с идентификатором id{}", itemOwnerId);
                break;
            case FUTURE:
                requestedBookings = bookingRepository.findAllFutureBookingsForItemOwnerById(itemOwnerId, page);
                log.info("Запрошен список всех предстоящих бронирований владельца с идентификатором id{}", itemOwnerId);
                break;
            case WAITING:
                requestedBookings = bookingRepository.findAllByItemUserIdAndStatus(itemOwnerId, "WAITING", page);
                log.info("Запрошен список всех 'WAITING' бронирований владельца с идентификатором id{}", itemOwnerId);
                break;
            case REJECTED:
                requestedBookings = bookingRepository.findAllByItemUserIdAndStatus(itemOwnerId, "REJECTED", page);
                log.info("Запрошен список всех 'REJECTED' бронирований владельца с идентификатором id{}", itemOwnerId);
                break;
            default:
                throw new InternalLogicException("Unknown state: " + bookingState.name());
        }
        return requestedBookings.map(objectMapper::fromDbEntity).map(objectMapper::toRestView);
    }

    private Page<BookingRestView> getRestViewsForBookerWithIdAndState(
            BookingState bookingState, long userId, Pageable page) {
        Page<BookingEntity> requestedBookings;
        switch (bookingState) {
            case ALL:
                requestedBookings = bookingRepository.findAllByUserId(userId, page);
                log.info("Запрошен список всех бронирований пользователя с идентификатором id{}", userId);
                break;
            case CURRENT:
                requestedBookings = bookingRepository.findAllCurrentBookingsForUserById(userId, page);
                log.info("Запрошен список всех текущих бронирований пользователя с идентификатором id{}", userId);
                break;
            case PAST:
                requestedBookings = bookingRepository.findAllPastBookingsForUserById(userId, page);
                log.info("Запрошен список всех прошедших бронирований пользователя с идентификатором id{}", userId);
                break;
            case FUTURE:
                requestedBookings = bookingRepository.findAllFutureBookingsForUserById(userId, page);
                log.info("Запрошен список всех предстоящих бронирований пользователя с идентификатором id{}", userId);
                break;
            case WAITING:
                requestedBookings = bookingRepository.findAllByUserIdAndStatus(userId, "WAITING", page);
                log.info("Запрошен список всех 'WAITING' бронирований пользователя с идентификатором id{}", userId);
                break;
            case REJECTED:
                requestedBookings = bookingRepository.findAllByUserIdAndStatus(userId, "REJECTED", page);
                log.info("Запрошен список всех 'REJECTED' бронирований пользователя с идентификатором id{}", userId);
                break;
            default:
                throw new InternalLogicException("Unknown state: " + bookingState.name());
        }
        return requestedBookings.map(objectMapper::fromDbEntity).map(objectMapper::toRestView);
    }

}