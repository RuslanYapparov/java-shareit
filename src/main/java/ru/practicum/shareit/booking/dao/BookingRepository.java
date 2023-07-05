package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findAllByUserId(long userId, Sort sort);

    @Query("select be " +
            "from BookingEntity as be " +
            "where be.userId = ?1 and " +
            "be.start < now() and be.end > now()")
    List<BookingEntity> findAllCurrentBookingsForUserById(long userId, Sort sort);

    @Query("select be " +
            "from BookingEntity as be " +
            "where be.userId = ?1 and " +
            "be.end < now()")
    List<BookingEntity> findAllPastBookingsForUserById(long userId, Sort sort);

    @Query("select be " +
            "from BookingEntity as be " +
            "where be.userId = ?1 and " +
            "be.start > now()")
    List<BookingEntity> findAllFutureBookingsForUserById(long userId, Sort sort);

    List<BookingEntity> findAllByUserIdAndStatus(long userId, String status, Sort sort);

    List<BookingEntity> findAllByItemUserId(long itemUserId, Sort sort);

    @Query("select be " +
            "from BookingEntity as be " +
            "join be.item as it " +
            "where it.userId = ?1 and " +
            "be.start < now() and be.end > now()")
    List<BookingEntity> findAllCurrentBookingsForItemOwnerById(long itemOwnerId, Sort sort);

    @Query("select be " +
            "from BookingEntity as be " +
            "join be.item as it " +
            "where it.userId = ?1 and " +
            "be.end < now()")
    List<BookingEntity> findAllPastBookingsForItemOwnerById(long itemOwnerId, Sort sort);

    @Query("select be " +
            "from BookingEntity as be " +
            "join be.item as it " +
            "where it.userId = ?1 and " +
            "be.start > now()")
    List<BookingEntity> findAllFutureBookingsForItemOwnerById(long itemOwnerId, Sort sort);

    List<BookingEntity> findAllByItemUserIdAndStatus(long itemOwnerId, String status, Sort sort);

}