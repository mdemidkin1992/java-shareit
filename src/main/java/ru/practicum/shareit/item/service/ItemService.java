package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto createItem(long ownerId, ItemDto itemDto);

    ItemDto getItemById(long itemId);

    List<ItemDto> getItemsByOwnerId(long ownerId);

    List<ItemDto> getItems();

    ItemDto updateItem(long itemId, long ownerId, Map<String, String> fields);

    List<ItemDto> searchItems(String text);

    void deleteItem(long itemId);
}
