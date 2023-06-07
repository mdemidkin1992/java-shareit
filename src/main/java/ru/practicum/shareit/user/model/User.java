package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class User {

    @NotNull
    long id;

    String name;

    @NotNull
    @Email(message = "User email is of invalid format")
    String email;
}
