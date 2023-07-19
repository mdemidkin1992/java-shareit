package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemResponseForRequest;

import java.util.List;

@Data
@Builder
public class ItemRequestInfoWithItems {
    private long id;
    private String description;
    private String created;
    private List<ItemResponseForRequest> items;
}
