package ru.practicum.shareit.user.address.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@RequiredArgsConstructor
@Builder
public class UserAddressRestView {
    @JsonProperty("userId")
    private final long userId;
    @JsonProperty("country")
    private final String country;
    @JsonProperty("region")
    private final String region;
    @JsonProperty("cityOrSettlement")
    private final String cityOrSettlement;
    @JsonProperty("cityDistrict")
    private final String cityDistrict;
    @JsonProperty("street")
    private final String street;
    @JsonProperty("houseNumber")
    private final int houseNumber;

}