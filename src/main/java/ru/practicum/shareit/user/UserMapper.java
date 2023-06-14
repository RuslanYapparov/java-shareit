package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import ru.practicum.shareit.common.ObjectMapper;
import ru.practicum.shareit.user.address.UserAddressMapper;
import ru.practicum.shareit.user.dto.UserRestCommand;
import ru.practicum.shareit.user.dto.UserRestView;

@Component
@RequiredArgsConstructor
public class UserMapper implements ObjectMapper<User, UserRestCommand, UserRestView> {
    // В задании было указано написать мэпперы для Item и User самостоятельно, поэтому не использую mapstruct
    private final UserAddressMapper userAddressMapper;

    @Override
    public User fromRestCommand(UserRestCommand userRestCommand) {
        userRestCommand.insertDefaultValuesInNullFields();
        User user = User.builder()
                .name(userRestCommand.getName())
                .email(userRestCommand.getEmail())
                .userItemsIds(userRestCommand.getUserItemsIds())
                .address(userAddressMapper.fromRestCommand(userRestCommand.getAddress()))
                .telephoneNumber(userRestCommand.getTelephoneNumber())
                .isTelephoneVisible(userRestCommand.isTelephoneVisible())
                .avatarUri(userRestCommand.getAvatarUri())
                .registrationDate(userRestCommand.getRegistrationDate())
                .userRating(userRestCommand.getUserRating())
                .build();
        user.setId(userRestCommand.getId());
        return user;
    }

    @Override
    public UserRestView toRestView(User user) {
        UserRestView userRestView = new UserRestView();
        userRestView.setId(user.getId());
        userRestView.setName(user.getName());
        userRestView.setEmail(user.getEmail());
        userRestView.setUserItemsIds(user.getUserItemsIds());
        userRestView.setAddress(userAddressMapper.toRestView(user.getAddress()));
        userRestView.setTelephoneNumber(user.getTelephoneNumber());
        userRestView.setTelephoneVisible(user.isTelephoneVisible());
        userRestView.setAvatarUri(user.getAvatarUri());
        userRestView.setRegistrationDate(user.getRegistrationDate());
        userRestView.setUserRating(user.getUserRating());
        return userRestView;
    }

}