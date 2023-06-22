package ru.practicum.shareit.user.address;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class UserAddress {
    Long userId;
    String country;
    String region;
    String cityOrSettlement;
    String cityDistrict;
    String street;
    int houseNumber;

}