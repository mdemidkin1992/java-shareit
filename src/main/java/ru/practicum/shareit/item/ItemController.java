package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                              @NotNull @RequestBody @Valid ItemDto itemDto) {
        log.info("POST request received for item: {}", itemDto);
        ItemDto response = itemService.createItem(ownerId, itemDto);
        log.info("Item created: {}", response);
        return response;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        log.info("GET request received for item with id: {}", itemId);
        ItemDto response = itemService.getItemById(itemId);
        log.info("{}", response);
        return response;
    }

    @GetMapping
    public List<ItemDto> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("GET request received for items with owner id: {}", ownerId);
        List<ItemDto> response = itemService.getItemsByOwnerId(ownerId);
        log.info("{}", response);
        return response;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") long ownerId,
                              @RequestBody ItemDto itemDto) {
        log.info("PATCH request received item with id: {}", itemId);
        ItemDto response = itemService.updateItem(itemId, ownerId, itemDto);
        log.info("Updated user: {}", response);
        return response;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("GET request received for query \"{}\"", text);
        List<ItemDto> response = itemService.searchItems(text.toLowerCase());
        log.info("{}", response);
        return response;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {
        log.info("DELETE request received item with id: {}", itemId);
        itemService.deleteItem(itemId);
        log.info("Item with id {} deleted", itemId);
    }

}