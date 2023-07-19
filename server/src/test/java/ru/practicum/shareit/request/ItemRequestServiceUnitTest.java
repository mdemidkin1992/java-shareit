package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.item.dto.ItemResponseForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDescription;
import ru.practicum.shareit.request.dto.ItemRequestInfo;
import ru.practicum.shareit.request.dto.ItemRequestInfoWithItems;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceUnitTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @Test
    void createNewItemRequest_whenValid_thenReturnRequest() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        ItemRequestDescription itemRequestDescription = new ItemRequestDescription();
        itemRequestDescription.setDescription("description");
        ItemRequest itemRequest = new ItemRequest();
        long itemRequestId = 1L;
        itemRequest.setId(itemRequestId);
        itemRequest.setDescription(itemRequestDescription.getDescription());
        itemRequest.setRequester(user);
        itemRequest.setCreated(ZonedDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestInfo expected = ItemRequestMapper.toItemRequestDto(itemRequest);
        ItemRequestInfo actual = itemRequestService.createNewItemRequest(userId, itemRequestDescription);
        assertEquals(expected, actual);
    }

    @Test
    void createNewItemRequest_whenUserNotFound_thenUserNotFoundException() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.createNewItemRequest(
                        1L,
                        new ItemRequestDescription()
                )
        );
    }

    @Test
    void getUserItemRequests_whenValid_thenReturnItemRequestWithItems() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        ItemRequestDescription itemRequestDescription = new ItemRequestDescription();
        itemRequestDescription.setDescription("description");
        ItemRequest itemRequest = new ItemRequest();
        long itemRequestId = 1L;
        itemRequest.setId(itemRequestId);
        itemRequest.setDescription(itemRequestDescription.getDescription());
        itemRequest.setRequester(user);
        itemRequest.setCreated(ZonedDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.getItemDescriptionForRequest(itemRequestId)).thenReturn(Collections.emptyList());

        List<ItemRequestInfoWithItems> expected =
                Stream.of(itemRequest)
                .map(ItemRequestMapper::toItemRequestWithItemsDto)
                .collect(Collectors.toList());
        for (ItemRequestInfoWithItems r : expected) {
            List<ItemResponseForRequest> items = itemRepository.getItemDescriptionForRequest(r.getId());
            r.setItems(items);
        }
        List<ItemRequestInfoWithItems> actual = itemRequestService.getUserItemRequests(userId);
        assertEquals(expected, actual);
    }

    @Test
    void getUserItemRequests_whenUserNotFound_thenUserNotFoundException() {
        long userId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getUserItemRequests(
                        userId
                )
        );
    }

    @Test
    void getOtherUsersItemRequests_whenValid_thenReturnItemRequestWithItems() {
        int from = 0, size = 10;
        Pageable page = PageRequest.of(from / size, size, Sort.by("created").descending());
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        ItemRequestDescription itemRequestDescription = new ItemRequestDescription();
        itemRequestDescription.setDescription("description");
        ItemRequest itemRequest = new ItemRequest();
        long itemRequestId = 1L;
        itemRequest.setId(itemRequestId);
        itemRequest.setDescription(itemRequestDescription.getDescription());
        itemRequest.setRequester(user);
        itemRequest.setCreated(ZonedDateTime.now());

        List<ItemRequest> itemRequests = List.of(itemRequest);
        Page<ItemRequest> itemRequestPage = new PageImpl<>(itemRequests, page, itemRequests.size());

        when(itemRequestRepository.findByRequesterIdIsNot(userId, page))
                .thenReturn(itemRequestPage);
        when(itemRepository.getItemDescriptionForRequest(itemRequestId)).thenReturn(Collections.emptyList());

        List<ItemRequestInfoWithItems> expected =
                Stream.of(itemRequest)
                        .map(ItemRequestMapper::toItemRequestWithItemsDto)
                        .collect(Collectors.toList());
        for (ItemRequestInfoWithItems r : expected) {
            List<ItemResponseForRequest> items = itemRepository.getItemDescriptionForRequest(r.getId());
            r.setItems(items);
        }
        List<ItemRequestInfoWithItems> actual = itemRequestService.getOtherUsersItemRequests(userId, from, size);
        assertEquals(expected, actual);
    }

    @Test
    void getItemRequestById_whenValid_thenReturnItemRequest() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        ItemRequestDescription itemRequestDescription = new ItemRequestDescription();
        itemRequestDescription.setDescription("description");
        ItemRequest itemRequest = new ItemRequest();
        long itemRequestId = 1L;
        itemRequest.setId(itemRequestId);
        itemRequest.setDescription(itemRequestDescription.getDescription());
        itemRequest.setRequester(user);
        itemRequest.setCreated(ZonedDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.getItemDescriptionForRequest(itemRequestId)).thenReturn(Collections.emptyList());

        ItemRequestInfoWithItems expected = ItemRequestMapper.toItemRequestWithItemsDto(itemRequest);
        expected.setItems(Collections.emptyList());
        ItemRequestInfoWithItems actual = itemRequestService.getItemRequestById(userId, itemRequestId);
        assertEquals(expected, actual);
    }

    @Test
    void getItemRequestById_whenValid_thenReturnItemRequestWithItemsList() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        ItemRequestDescription itemRequestDescription = new ItemRequestDescription();
        itemRequestDescription.setDescription("description");
        ItemRequest itemRequest = new ItemRequest();
        long itemRequestId = 1L;
        itemRequest.setId(itemRequestId);
        itemRequest.setDescription(itemRequestDescription.getDescription());
        itemRequest.setRequester(user);
        itemRequest.setCreated(ZonedDateTime.now());

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(user);
        item.setId(itemId);
        item.setAvailable(true);
        item.setDescription("description");
        item.setRequest(itemRequest);

        ItemResponseForRequest itemResponseForRequest = new ItemResponseForRequest(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest().getId()
        );


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.getItemDescriptionForRequest(itemRequestId)).thenReturn(List.of(itemResponseForRequest));

        ItemRequestInfoWithItems expected = ItemRequestMapper.toItemRequestWithItemsDto(itemRequest);
        expected.setItems(List.of(itemResponseForRequest));
        ItemRequestInfoWithItems actual = itemRequestService.getItemRequestById(userId, itemRequestId);
        assertEquals(expected, actual);
    }

    @Test
    void getItemRequestById_whenUserNotFound_thenUserNotFoundException() {
        long userId = 1L, itemRequestId = 1L;
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getItemRequestById(
                        userId,
                        itemRequestId
                )
        );
    }

    @Test
    void getItemRequestById_whenItemRequestNotFound_thenItemRequestNotFoundException() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);
        long itemRequestId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.empty());
        assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(
                        userId,
                        itemRequestId
                )
        );
    }
}