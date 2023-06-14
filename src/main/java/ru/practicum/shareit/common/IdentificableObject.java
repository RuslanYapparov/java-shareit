package ru.practicum.shareit.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public abstract class IdentificableObject {
    @Getter
    @Setter
    protected long id;

}