package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemRepository {
    ItemDto createItem(long ownerId, ItemDto itemDto);

    ItemDto getItemById(long itemId);

    List<ItemDto> getItemsByOwnerId(long ownerId);

    List<ItemDto> getAllItems();

    ItemDto updateItem(long itemId, long ownerId, ItemDto itemDto);

    List<ItemDto> searchItems(String text);

    void deleteItem(long itemId);
}
