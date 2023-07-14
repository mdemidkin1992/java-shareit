package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping(path = "/items")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @NotNull @RequestBody @Valid ItemDto itemDto
    ) {
        log.info("Item created: {}", itemDto);
        return itemClient.createItem(ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long ownerId
    ) {
        log.info("Get item {}, user: {}", itemId, ownerId);
        return itemClient.getItem(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwnerId(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
            @RequestParam(required = false, defaultValue = "10") @Min(0) int size
    ) {
        log.info("GET request received for items with owner id: {}", ownerId);
        return itemClient.getItemsByOwnerId(ownerId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("PATCH request received item with id: {}", itemId);
        return itemClient.updateItem(itemId, ownerId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam String text,
            @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
            @RequestParam(required = false, defaultValue = "10") @Min(0) int size
    ) {
        log.info("GET request received for query \"{}\"", text);
        return itemClient.searchItems(text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(
            @PathVariable long itemId
    ) {
        log.info("DELETE request received item with id: {}", itemId);
        log.info("Item with id {} deleted", itemId);
        return itemClient.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createCommentToItem(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestBody CommentRequestDto commentRequestDto
    ) {
        log.info("POST request received to post comment for item {}", itemId);
        log.info("Comment {} from user {} for item {} created", commentRequestDto.getText(), bookerId, itemId);
        return itemClient.createCommentToItem(itemId, bookerId, commentRequestDto);
    }

}
