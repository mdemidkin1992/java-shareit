package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponseForRequest {
    private final Long id;
    private final String name;
    private final String description;
    private final boolean available;
    private final long requestId;

    public ItemResponseForRequest(Long id, String name, String description, boolean available, long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
