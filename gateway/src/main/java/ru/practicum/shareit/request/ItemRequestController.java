package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDescription;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@Validated
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid ItemRequestDescription request
    ) {
        log.info("POST request received for Item Request: {}", request);
        return itemRequestClient.createItemRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        log.info("GET request received for all Item Request of user: {}", userId);
        return itemRequestClient.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersItemRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
            @RequestParam(required = false, defaultValue = "10") @Min(0) int size
    ) {
        log.info("GET request received for other Item Request");
        return itemRequestClient.getOtherUsersItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable(name = "requestId") long requestId
    ) {
        log.info("GET request received for Item Request: {}", requestId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

}
