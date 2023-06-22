package ru.practicum.shareit.user.address;

import org.mapstruct.Mapper;

import ru.practicum.shareit.user.address.dto.UserAddressRestCommand;
import ru.practicum.shareit.user.address.dto.UserAddressRestView;

@Mapper(componentModel = "spring")
public interface UserAddressMapper {

    UserAddress fromRestCommand(UserAddressRestCommand userAddressRestCommand);

    UserAddressRestView toRestView(UserAddress userAddress);

}