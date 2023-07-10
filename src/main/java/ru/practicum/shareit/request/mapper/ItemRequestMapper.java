package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestDescription;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.dto.ItemRequestInfoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequest fromItemRequestDto(ItemRequestDescription itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }

    public static ItemRequestInfo toItemRequestDto(ItemRequest itemRequest) {
        String created = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .format(itemRequest.getCreated());

        return ItemRequestInfo.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(created)
                .build();
    }

    public static ItemRequestInfoWithItems toItemRequestWithItemsDto(ItemRequest itemRequest) {
        String created = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .format(itemRequest.getCreated());

        return ItemRequestInfoWithItems.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(created)
                .build();
    }
}
