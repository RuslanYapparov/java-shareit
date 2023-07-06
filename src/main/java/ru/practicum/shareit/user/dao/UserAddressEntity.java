package ru.practicum.shareit.user.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Embeddable
@Table(name = "users")
public class UserAddressEntity {
    private String country;
    private String region;
    @Column(name = "city_or_settlement")
    private String cityOrSettlement;
    @Column(name = "city_district")
    private String cityDistrict;
    private String street;
    @Column(name = "house_number")
    private int houseNumber;

}