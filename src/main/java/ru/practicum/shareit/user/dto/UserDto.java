package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {

    private long id;

    private String name;

    @NotNull
    @Email(message = "User email is of invalid format")
    private String email;
}
