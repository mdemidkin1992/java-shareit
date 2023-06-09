package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.AccessDenyException;
import ru.practicum.shareit.util.exception.ItemNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> itemMap = new HashMap<>();
    private long count = 0;

    @Autowired
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(long ownerId, ItemDto itemDto) {
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(UserMapper.fromUserDto(userRepository.getUserById(ownerId)));
        item.setId(++count);
        itemMap.put(item.getId(), item);
        return ItemMapper.toItemDto(itemMap.get(item.getId()));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        if (itemMap.get(itemId) == null) {
            String message = String.format("Item with id %s not found", itemId);
            throw new ItemNotFoundException(message);
        }
        return ItemMapper.toItemDto(itemMap.get(itemId));
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(long ownerId) {
        return itemMap.values().stream()
                .filter(i -> i.getOwner().getId() == ownerId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItems() {
        return itemMap.values().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(long itemId, long ownerId, Map<String, String> fields) {
        try {
            Item targetItem = itemMap.get(itemId);
            if (targetItem == null) {
                String message = String.format("Item with id %s not found", itemId);
                throw new ItemNotFoundException(message);
            }
            if (targetItem.getOwner().getId() != ownerId) {
                String message = String.format("User %s is not owner of item %s", ownerId, itemId);
                throw new AccessDenyException(message);
            }
            updateItemFields(targetItem, fields);
            itemMap.put(targetItem.getId(), targetItem);
        } catch (ItemNotFoundException e) {
            String message = String.format("Item with id %s not found", itemId);
            throw new ItemNotFoundException(message);
        }
        return ItemMapper.toItemDto(itemMap.get(itemId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.equals("")) {
            return new ArrayList<>();
        }
        return itemMap.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(text)
                        || i.getDescription().toLowerCase().contains(text))
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(long itemId) {
        getItemById(itemId);
        itemMap.remove(itemId);
    }

    private void updateItemFields(Item item, Map<String, String> fields) {
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            switch (entry.getKey()) {
                case "name":
                    item.setName(entry.getValue());
                    break;
                case "description":
                    item.setDescription(entry.getValue());
                    break;
                case "available":
                    item.setAvailable(Boolean.parseBoolean(entry.getValue()));
                    break;
            }
        }
    }
}
