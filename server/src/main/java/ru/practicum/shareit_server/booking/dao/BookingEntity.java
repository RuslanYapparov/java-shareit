package ru.practicum.shareit_server.booking.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

import ru.practicum.shareit_server.common.UpdatableUserDependedEntity;
import ru.practicum.shareit_server.item.dao.ItemEntity;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "bookings")
public class BookingEntity extends UpdatableUserDependedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    protected long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    ItemEntity item;
    String status;
    @Column(name = "booking_start")
    LocalDateTime start;
    @Column(name = "booking_end")
    LocalDateTime end;

}