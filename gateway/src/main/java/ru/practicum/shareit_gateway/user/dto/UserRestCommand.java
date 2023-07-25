package ru.practicum.shareit_gateway.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder(toBuilder = true)
public class UserRestCommand {
    @NotNull
    @NotBlank
    @JsonProperty("name")
    String name;
    @NotNull
    @NotBlank
    @Email
    @JsonProperty("email")
    String email;

}