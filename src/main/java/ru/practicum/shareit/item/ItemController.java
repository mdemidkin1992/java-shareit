package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @NotNull @RequestBody @Valid ItemDto itemDto
    ) {
        log.info("POST request received for item: {}", itemDto);
        ItemDto response = itemService.createItem(ownerId, itemDto);
        log.info("Item created: {}", response);
        return response;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long ownerId
    ) {
        log.info("GET request received for item with id: {}", itemId);
        ItemDto response = itemService.getItemById(itemId, ownerId);
        log.info("{}", response);
        return response;
    }

    @GetMapping
    public List<ItemDto> getItemsByOwnerId(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
            @RequestParam(required = false, defaultValue = "10") @Min(0) int size
    ) {
        log.info("GET request received for items with owner id: {}", ownerId);
        List<ItemDto> response = itemService.getItemsByOwnerId(ownerId, from, size);
        log.info("{}", response);
        return response;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("PATCH request received item with id: {}", itemId);
        ItemDto response = itemService.updateItem(itemId, ownerId, itemDto);
        log.info("Updated user: {}", response);
        return response;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam String text,
            @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
            @RequestParam(required = false, defaultValue = "10") @Min(0) int size
    ) {
        log.info("GET request received for query \"{}\"", text);
        List<ItemDto> response = itemService.searchItems(text.toLowerCase(), from, size);
        log.info("{}", response);
        return response;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @PathVariable long itemId
    ) {
        log.info("DELETE request received item with id: {}", itemId);
        itemService.deleteItem(itemId);
        log.info("Item with id {} deleted", itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createCommentToItem(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestBody CommentRequestDto commentRequestDto
    ) {
        log.info("POST request received to post comment for item {}", itemId);
        CommentResponseDto response = itemService.addComment(commentRequestDto, bookerId, itemId);
        log.info("Comment {} from user {} for item {} created", commentRequestDto.getText(), bookerId, itemId);
        return response;
    }

}