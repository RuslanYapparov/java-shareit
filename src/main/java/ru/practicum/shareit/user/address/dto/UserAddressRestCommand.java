package ru.practicum.shareit.user.address.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserAddressRestCommand {
    private long userId;
    private String country;
    private String region;
    private String cityOrSettlement;
    private String cityDistrict;
    private String street;
    private int houseNumber;

}