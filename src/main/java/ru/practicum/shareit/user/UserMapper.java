package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import ru.practicum.shareit.common.RestCommandObjectValidator;
import ru.practicum.shareit.user.address.UserAddressMapper;
import ru.practicum.shareit.user.dto.UserRestCommand;
import ru.practicum.shareit.user.dto.UserRestView;

@Component
@RequiredArgsConstructor
public class UserMapper {
    // В задании было указано написать мэпперы для Item и User самостоятельно, поэтому не использую mapstruct
    private final UserAddressMapper userAddressMapper;
    private final RestCommandObjectValidator validator;

    public User fromRestCommand(UserRestCommand userRestCommand) {
        userRestCommand = validator.validate(userRestCommand);
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

    public UserRestView toRestView(User user) {
        return UserRestView.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .userItemsIds(user.getUserItemsIds())
                .address(userAddressMapper.toRestView(user.getAddress()))
                .telephoneNumber(user.getTelephoneNumber())
                .isTelephoneVisible(user.isTelephoneVisible())
                .avatarUri(user.getAvatarUri())
                .registrationDate(user.getRegistrationDate())
                .userRating(user.getUserRating())
                .build();
    }

}