package ru.practicum.shareit.user.address;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class UserAddress {
    long userId;
    String country;
    String region;
    String cityOrSettlement;
    String cityDistrict;
    String street;
    int houseNumber;

}