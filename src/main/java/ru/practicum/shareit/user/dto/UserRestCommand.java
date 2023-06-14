package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import ru.practicum.shareit.exception.BadRequestBodyException;
import ru.practicum.shareit.exception.BadRequestParameterException;
import ru.practicum.shareit.common.Receivable;
import ru.practicum.shareit.common.ShareItConstants;
import ru.practicum.shareit.user.address.dto.UserAddressRestCommand;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
public class UserRestCommand implements Receivable<UserRestCommand> {
    @PositiveOrZero
    private long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @Email
    private String email;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    private Set<Long> userItemsIds;
    private UserAddressRestCommand address;
    private long telephoneNumber;
    private boolean isTelephoneVisible;
    private URI avatarUri;
    private LocalDate registrationDate;
    private float userRating;     // Наверное, пользователей (владельцев и арендаторов) имеет смысл оценивать другим

    @Override
    public UserRestCommand insertDefaultValuesInNullFields()
            throws BadRequestParameterException, BadRequestBodyException {
        checkFieldsForDefaultAndIncorrectValues();
        name = (name == null) ? ShareItConstants.NOT_ASSIGNED : name;
        email = (email == null) ? ShareItConstants.NOT_ASSIGNED : email;
        userItemsIds = (userItemsIds == null) ? new HashSet<>() : userItemsIds;
        address = (address == null) ? new UserAddressRestCommand().insertDefaultValuesInNullFields() :
                                              address.insertDefaultValuesInNullFields();
        avatarUri = (avatarUri == null) ? ShareItConstants.DEFAULT_URI : avatarUri;
        registrationDate = (registrationDate == null) ? ShareItConstants.DEFAULT_DATE : registrationDate;
        return this;
    }

    @Override
    public void checkFieldsForDefaultAndIncorrectValues() throws BadRequestParameterException, BadRequestBodyException {
        if (id < 0) {
            throw new BadRequestParameterException("Указан неверный идентификатор пользователя в HTTP-запросе - " +
                    "идентификатор должен быть положительным числом");
        }
        if (name != null && name.equals(ShareItConstants.NOT_ASSIGNED)) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить пользователя с именем '%s'. " +
                    "Данное обозначение зарезервино системой", name));
        }
        if (email != null && email.equals(ShareItConstants.NOT_ASSIGNED)) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить пользователя с адресом " +
                    "электронной почты '%s'. Данное обозначение зарезервино системой", email));
        }
        if (avatarUri != null && avatarUri.equals(ShareItConstants.DEFAULT_URI)) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить пользователя с указанным URI " +
                    "фотографии аватара'%s'. Данное обозначение зарезервино системой", avatarUri));
        }
        if (registrationDate != null && registrationDate.equals(ShareItConstants.DEFAULT_DATE)) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить пользователя с указанной датой " +
                    "регистрации '%s'. Данное обозначение зарезервино системой",
                    registrationDate.format(ShareItConstants.DATE_FORMATTER)));
        }
    }

}