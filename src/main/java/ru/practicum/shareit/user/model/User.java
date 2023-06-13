package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class User {

    private long id;

    private String name;

    @NotNull
    @Email(message = "User email is of invalid format")
    private String email;
}
