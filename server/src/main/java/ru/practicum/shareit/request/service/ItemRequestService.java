package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDescription;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.dto.ItemRequestInfoWithItems;

import java.util.List;

public interface ItemRequestService {
    ItemRequestInfo createNewItemRequest(long userId, ItemRequestDescription request);

    List<ItemRequestInfoWithItems> getUserItemRequests(long userId);

    List<ItemRequestInfoWithItems> getOtherUsersItemRequests(long userId, int from, int size);

    ItemRequestInfoWithItems getItemRequestById(long userId, long requestId);
}
