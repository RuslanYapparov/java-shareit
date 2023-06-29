package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingEntity;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingRestCommand;
import ru.practicum.shareit.booking.dto.BookingRestView;
import ru.practicum.shareit.common.CrudServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.ItemEntity;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

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
                              BookingMapper bookingMapper,
                              BookingValidator bookingValidator) {
        this.entityRepository = bookingRepository;
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.objectMapper = bookingMapper;
        this.domainObjectValidator = bookingValidator;
        this.type = "booking";
    }

    @Override
    public BookingRestView save(long userId, BookingRestCommand bookingRestCommand) {
        ItemEntity savedItemEntity = checkBookingRestCommandAndReturnItemEntity(userId, bookingRestCommand);
        bookingRestCommand = bookingRestCommand.toBuilder().bookerId(userId).build();
        Booking booking = objectMapper.fromRestCommand(bookingRestCommand);
        booking = booking.toBuilder()
                .bookingStatus(BookingStatus.WAITING)
                .build();
        BookingEntity bookingEntity = objectMapper.toDbEntity(booking);
        bookingEntity.setItem(savedItemEntity);
        bookingEntity = bookingRepository.save(bookingEntity);
        booking = objectMapper.fromDbEntity(bookingEntity);
        log.info("Пользователь с идентификатором id{} забронировал вещь '{}' с идентификатором id{} в период с {} по {}." +
                        "Объекту '{}' присвоен идентификатор id{}", userId, savedItemEntity.getName(),
                booking.getItemId(), booking.getStart(), booking.getEnd(), type, booking.getId());
        return objectMapper.toRestView(booking);
    }

    @Override
    public BookingRestView changeBookingStatus(long userId, long bookingId, boolean isApproved) {
        BookingEntity bookingEntity = checkUserAndObjectExistingAndReturnEntityFromDb(userId, bookingId);
        long itemOwnerId = bookingEntity.getItem().getUserId();
        if (userId != itemOwnerId) {
            throw new ObjectNotFoundException(String.format("Операция изменения статуса бронирования с " +
                    "идентификатором id'%d' отклонена: только владелец вещи может изменять статус", bookingId));
        }
        String bookingStatus = bookingEntity.getStatus();
        if (isApproved & "APPROVED".equals(bookingStatus)) {
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
    public List<BookingRestView> getAllForBookerWithStateParameter(long userId, String state) {
        checkUserExistingAndReturnUserShort(userId);
        List<Booking> requestedBookings;
        switch (state) {
            case "ALL":
                requestedBookings = entitiesToObjectsListTransducer.apply(bookingRepository
                        .findAllByUserId(userId, sortByBookingStart));
                log.info("Запрошен список всех бронирований пользователя с идентификатором id{}", userId);
                break;
            case "CURRENT":
                requestedBookings = entitiesToObjectsListTransducer.apply(bookingRepository
                        .findAllCurrentBookingsForUserById(userId, sortByBookingStart));
                log.info("Запрошен список всех текущих бронирований пользователя с идентификатором id{}", userId);
                break;
            case "PAST":
                requestedBookings = entitiesToObjectsListTransducer.apply(bookingRepository
                        .findAllPastBookingsForUserById(userId, sortByBookingStart));
                log.info("Запрошен список всех прошедших бронирований пользователя с идентификатором id{}", userId);
                break;
            case "FUTURE":
                requestedBookings = entitiesToObjectsListTransducer.apply(bookingRepository
                        .findAllFutureBookingsForUserById(userId, sortByBookingStart));
                log.info("Запрошен список всех предстоящих бронирований пользователя с идентификатором id{}", userId);
                break;
            case "WAITING":
                requestedBookings = entitiesToObjectsListTransducer.apply(bookingRepository
                        .findAllByUserIdAndStatus(userId, "WAITING", sortByBookingStart));
                log.info("Запрошен список всех 'WAITING' бронирований пользователя с идентификатором id{}", userId);
                break;
            case "REJECTED":
                requestedBookings = entitiesToObjectsListTransducer.apply(bookingRepository
                        .findAllByUserIdAndStatus(userId, "REJECTED", sortByBookingStart));
                log.info("Запрошен список всех 'REJECTED' бронирований пользователя с идентификатором id{}", userId);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
        return objectsToRestViewsListTransducer.apply(requestedBookings);
    }

    @Override
    public List<BookingRestView> getAllForItemOwnerWithStateParameter(long itemOwnerId, String state) {
        checkUserExistingAndReturnUserShort(itemOwnerId);
        List<Booking> requestedBookings;
        switch (state) {
            case "ALL":
                requestedBookings = entitiesToObjectsListTransducer.apply(bookingRepository
                        .findAllByItemUserId(itemOwnerId, sortByBookingStart));
                log.info("Запрошен список всех бронирований вещи владельца с идентификатором id{}", itemOwnerId);
                break;
            case "CURRENT":
                requestedBookings = entitiesToObjectsListTransducer.apply(bookingRepository
                        .findAllCurrentBookingsForItemOwnerById(itemOwnerId, sortByBookingStart));
                log.info("Запрошен список всех текущих бронирований владельца с идентификатором id{}", itemOwnerId);
                break;
            case "PAST":
                requestedBookings = entitiesToObjectsListTransducer.apply(bookingRepository
                        .findAllPastBookingsForItemOwnerById(itemOwnerId, sortByBookingStart));
                log.info("Запрошен список всех прошедших бронирований владельца с идентификатором id{}", itemOwnerId);
                break;
            case "FUTURE":
                requestedBookings = entitiesToObjectsListTransducer.apply(bookingRepository
                        .findAllFutureBookingsForItemOwnerById(itemOwnerId, sortByBookingStart));
                log.info("Запрошен список всех предстоящих бронирований владельца с идентификатором id{}", itemOwnerId);
                break;
            case "WAITING":
                requestedBookings = entitiesToObjectsListTransducer.apply(bookingRepository
                        .findAllByItemUserIdAndStatus(itemOwnerId, "WAITING", sortByBookingStart));
                log.info("Запрошен список всех 'WAITING' бронирований владельца с идентификатором id{}", itemOwnerId);
                break;
            case "REJECTED":
                requestedBookings = entitiesToObjectsListTransducer.apply(bookingRepository
                        .findAllByItemUserIdAndStatus(itemOwnerId, "REJECTED", sortByBookingStart));
                log.info("Запрошен список всех 'REJECTED' бронирований владельца с идентификатором id{}", itemOwnerId);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
        return objectsToRestViewsListTransducer.apply(requestedBookings);
    }

    @Override
    protected BookingEntity checkUserAndObjectExistingAndReturnEntityFromDb(long userId, long bookingId) {
        checkUserExistingAndReturnUserShort(userId);
        BookingEntity bookingEntity = entityRepository.findById(bookingId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("В ходе выполнения операции над объектом '%s' с " +
                        "идентификатором id%d произошла ошибка: объект ранее не был сохранен", type, bookingId)));
        if (userId != bookingEntity.getUserId() & userId != bookingEntity.getItem().getUserId()) {
            throw new ObjectNotFoundException(String.format("В ходе выполнения операции над объектом '%s' с " +
                    "идентификатором id%d произошла ошибка: пользователь с id%d не является владельцем вещи и " +
                    "не бронировал ее у другого пользователя", type, bookingId, userId));
        }
        return bookingEntity;
    }

    private ItemEntity checkBookingRestCommandAndReturnItemEntity(long userId, BookingRestCommand bookingRestCommand) {
        checkUserExistingAndReturnUserShort(userId);
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
            List<Booking> bookings = entitiesToObjectsListTransducer.apply(itemEntity.getItemBookings());
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

}