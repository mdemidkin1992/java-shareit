package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class Item {
    @NotNull
    long id;

    @NotNull
    String name;

    @NotNull
    String description;

    @NotNull
    Boolean available;

    User owner;
    ItemRequest request;
}
