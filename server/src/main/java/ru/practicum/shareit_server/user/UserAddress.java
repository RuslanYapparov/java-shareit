package ru.practicum.shareit_server.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Value
@Builder
public class UserAddress {
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