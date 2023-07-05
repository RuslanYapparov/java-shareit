package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.common.DomainObjectValidator;

@Component
public class BookingValidator implements DomainObjectValidator<Booking> {

    @Override
    public Booking validateAndAssignNullFields(Booking booking) {
        return booking;
    }

}