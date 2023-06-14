package ru.practicum.shareit.user.address.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@EqualsAndHashCode
@NoArgsConstructor
@Setter
public class UserAddressRestView {
    @JsonProperty("userId")
    long userId;
    @JsonProperty("country")
    String country;
    @JsonProperty("region")
    String region;
    @JsonProperty("cityOrSettlement")
    String cityOrSettlement;
    @JsonProperty("cityDistrict")
    String cityDistrict;
    @JsonProperty("street")
    String street;
    @JsonProperty("houseNumber")
    int houseNumber;

}