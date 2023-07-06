package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    Page<BookingEntity> findAllByUserId(long userId, Pageable page);

    @Query("select be " +
            "from BookingEntity as be " +
            "where be.userId = ?1 and " +
            "be.start < now() and be.end > now()")
    Page<BookingEntity> findAllCurrentBookingsForUserById(long userId, Pageable page);

    @Query("select be " +
            "from BookingEntity as be " +
            "where be.userId = ?1 and " +
            "be.end < now()")
    Page<BookingEntity> findAllPastBookingsForUserById(long userId, Pageable page);

    @Query("select be " +
            "from BookingEntity as be " +
            "where be.userId = ?1 and " +
            "be.start > now()")
    Page<BookingEntity> findAllFutureBookingsForUserById(long userId, Pageable page);

    Page<BookingEntity> findAllByUserIdAndStatus(long userId, String status, Pageable page);

    Page<BookingEntity> findAllByItemUserId(long itemUserId, Pageable page);

    @Query("select be " +
            "from BookingEntity as be " +
            "join be.item as it " +
            "where it.userId = ?1 and " +
            "be.start < now() and be.end > now()")
    Page<BookingEntity> findAllCurrentBookingsForItemOwnerById(long itemOwnerId, Pageable page);

    @Query("select be " +
            "from BookingEntity as be " +
            "join be.item as it " +
            "where it.userId = ?1 and " +
            "be.end < now()")
    Page<BookingEntity> findAllPastBookingsForItemOwnerById(long itemOwnerId, Pageable page);

    @Query("select be " +
            "from BookingEntity as be " +
            "join be.item as it " +
            "where it.userId = ?1 and " +
            "be.start > now()")
    Page<BookingEntity> findAllFutureBookingsForItemOwnerById(long itemOwnerId, Pageable page);

    Page<BookingEntity> findAllByItemUserIdAndStatus(long itemOwnerId, String status, Pageable page);

}