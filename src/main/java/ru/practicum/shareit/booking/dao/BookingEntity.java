package ru.practicum.shareit.booking.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

import ru.practicum.shareit.common.UpdatableUserDependedEntity;
import ru.practicum.shareit.item.dao.ItemEntity;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "bookings")
public class BookingEntity extends UpdatableUserDependedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    ItemEntity item;
    String status;
    @Column(name = "booking_start")
    LocalDateTime start;
    @Column(name = "booking_end")
    LocalDateTime end;

}