package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public ItemDto createItem(long ownerId, ItemDto itemDto) {
        return itemRepository.createItem(ownerId, itemDto);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(long ownerId) {
        return itemRepository.getItemsByOwnerId(ownerId);
    }

    @Override
    public List<ItemDto> getItems() {
        return itemRepository.getAllItems();
    }

    @Override
    public ItemDto updateItem(long itemId, long ownerId, ItemDto itemDto) {
        return itemRepository.updateItem(itemId, ownerId, itemDto);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.searchItems(text);
    }

    @Override
    public void deleteItem(long itemId) {
        itemRepository.deleteItem(itemId);
    }

}
