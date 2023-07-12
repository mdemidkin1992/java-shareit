package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemResponseForRequest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDescription;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.dto.ItemRequestInfoWithItems;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestInfo createNewItemRequest(long userId, ItemRequestDescription request) {
        Optional<User> maybeUser = userRepository.findById(userId);
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(request);
            itemRequest.setRequester(user);
            itemRequest.setCreated(ZonedDateTime.now());
            itemRequest = itemRequestRepository.save(itemRequest);
            return ItemRequestMapper.toItemRequestDto(itemRequest);
        } else {
            throw new UserNotFoundException("User with id " + userId + " doesn't exist.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestInfoWithItems> getUserItemRequests(long userId) {
        Optional<User> mayBeUser = userRepository.findById(userId);
        if (mayBeUser.isPresent()) {
            List<ItemRequestInfoWithItems> requests = itemRequestRepository
                    .findAllByRequesterIdOrderByCreatedDesc(userId)
                    .stream()
                    .map(ItemRequestMapper::toItemRequestWithItemsDto)
                    .collect(Collectors.toList());

            for (ItemRequestInfoWithItems r : requests) {
                List<ItemResponseForRequest> items = getItemResponsesForRequest(r.getId());
                r.setItems(items);
            }
            return requests;
        } else {
            throw new UserNotFoundException("User with id " + userId + " was not found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestInfoWithItems> getOtherUsersItemRequests(long userId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequestInfoWithItems> requests = itemRequestRepository
                .findByRequesterIdIsNot(userId, page)
                .stream()
                .map(ItemRequestMapper::toItemRequestWithItemsDto)
                .collect(Collectors.toList());

        for (ItemRequestInfoWithItems r : requests) {
            List<ItemResponseForRequest> items = getItemResponsesForRequest(r.getId());
            r.setItems(items);
        }

        return requests;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestInfoWithItems getItemRequestById(long userId, long requestId) {
        Optional<User> maybeUser = userRepository.findById(userId);
        if (maybeUser.isEmpty()) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        Optional<ItemRequest> maybeItemRequest = itemRequestRepository.findById(requestId);
        if (maybeItemRequest.isPresent()) {
            ItemRequest itemRequest = maybeItemRequest.get();
            ItemRequestInfoWithItems response = ItemRequestMapper.toItemRequestWithItemsDto(itemRequest);
            List<ItemResponseForRequest> items = getItemResponsesForRequest(requestId);
            response.setItems(items);
            return response;
        } else {
            throw new ItemRequestNotFoundException("Item request " + requestId + " not found");
        }
    }

    private List<ItemResponseForRequest> getItemResponsesForRequest(long requestId) {
        List<ItemResponseForRequest> items = itemRepository.getItemDescriptionForRequest(requestId);
        if (!items.isEmpty()) {
            return items;
        }
        return Collections.emptyList();
    }

}
