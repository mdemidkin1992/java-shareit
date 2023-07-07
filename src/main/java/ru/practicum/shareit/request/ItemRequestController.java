package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDescription;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.dto.ItemRequestInfoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestInfo createItemRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid ItemRequestDescription request
    ) {
        log.info("POST request received for Item Request: {}", request);
        ItemRequestInfo response = itemRequestService.createNewItemRequest(userId, request);
        log.info("Item Request created: {}", response);
        return response;
    }

    @GetMapping
    public List<ItemRequestInfoWithItems> getUserItemRequests(
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        log.info("GET request received for all Item Request of user: {}", userId);
        List<ItemRequestInfoWithItems> response = itemRequestService.getUserItemRequests(userId);
        log.info("Item requests of user {}: {}", userId, response);
        return response;
    }

    @GetMapping("/all")
    public List<ItemRequestInfoWithItems> getOtherUsersItemRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
            @RequestParam(required = false, defaultValue = "10") @Min(0) int size
    ) {
        log.info("GET request received for other Item Request");
        List<ItemRequestInfoWithItems> response = itemRequestService.getOtherUsersItemRequests(userId, from, size);
        log.info("Other item requests: {}", response);
        return response;
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfoWithItems getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable(name = "requestId") long requestId
    ) {
        log.info("GET request received for Item Request: {}", requestId);
        ItemRequestInfoWithItems response = itemRequestService.getItemRequestById(userId, requestId);
        log.info("Item request {}: {}", requestId, response);
        return response;
    }

}
