package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingClosest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.CommentNotAuthorisedException;
import ru.practicum.shareit.util.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {

    @InjectMocks
    public ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private static final LocalDateTime CURRENT_TIMESTAMP = LocalDateTime.now();

    @Test
    void createItem_whenUserIdInvalid_thenUserNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> itemService.createItem(
                        1L,
                        ItemDto.builder().build()
                ));
    }

    @Test
    void createItem_whenUserIdCorrect_thenCreateItem() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        ItemDto itemDto = ItemDto.builder().build();
        Item item = new Item();
        long itemId = 1L;
        item.setOwner(user);
        item.setId(itemId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto expectItemDto = ItemMapper.toItemDto(item);
        ItemDto actualItemDto = itemService.createItem(userId, itemDto);
        assertEquals(expectItemDto, actualItemDto);
    }

    @Test
    void createItem_whenRequestIdIsPresent_thenCreateItemWithRequest() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        ItemDto itemDto = ItemDto.builder().build();
        Item item = new Item();
        long itemId = 1L;
        item.setId(itemId);
        item.setOwner(user);

        ItemRequest request = new ItemRequest();
        long requestId = 1L;
        request.setId(requestId);
        item.setRequest(request);
        itemDto.setRequestId(requestId);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(any())).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto expectItemDto = ItemMapper.toItemDto(item);
        ItemDto actualItemDto = itemService.createItem(userId, itemDto);
        assertEquals(expectItemDto, actualItemDto);
    }

    @Test
    void createItem_whenRequestIdInvalid_thenItemRequestNotFoundException() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(new User()));

        when(itemRequestRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(ItemRequestNotFoundException.class,
                () -> itemService.createItem(
                        1L,
                        ItemDto.builder().requestId(1L).build()
                ));
    }

//    @Test
//    void getItemById_whenUserIdAndItemIdValid_thenReturnItem() {
//        User user = new User();
//        long userId = 1L;
//        user.setId(userId);
//
//        Item item = new Item();
//        long itemId = 1L;
//        item.setId(itemId);
//        item.setOwner(user);
//
//        List<BookingClosest> nextBookingClosest = List.of(new BookingClosest());
//        List<BookingClosest> lastBookingClosest = List.of(new BookingClosest());
//
//        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
//        when(commentRepository.findAllByItemId(itemId)).thenReturn(Collections.emptyList());
//        when(bookingRepository.findNextClosestBookingByOwnerId(anyLong(), anyLong()))
//                .thenReturn(nextBookingClosest);
//        when(bookingRepository.findLastClosestBookingByOwnerId(anyLong(), anyLong()))
//                .thenReturn(lastBookingClosest);
//
//        ItemDto actualItemDto = ItemMapper.toItemDto(item);
//        actualItemDto.setComments(Collections.emptyList());
//        actualItemDto.setNextBooking(nextBookingClosest.get(0));
//        actualItemDto.setLastBooking(lastBookingClosest.get(0));
//        ItemDto expectItemDto = itemService.getItemById(itemId, userId);
//        assertEquals(actualItemDto, expectItemDto);
//    }

//    @Test
//    void getItemsByOwnerId_whenUserIdAndItemIdValid_thenReturnItemsList() {
//        int from = 0, size = 10;
//        Pageable page = PageRequest.of(from / size, size);
//
//        User user = new User();
//        long userId = 1L;
//        user.setId(userId);
//
//        Item item = new Item();
//        long itemId = 1L;
//        item.setId(itemId);
//        item.setOwner(user);
//
//        List<BookingClosest> nextBookingClosest = List.of(new BookingClosest());
//        List<BookingClosest> lastBookingClosest = List.of(new BookingClosest());
//
//        when(itemRepository.findAllByOwnerIdOrderById(userId, page)).thenReturn(List.of(item));
//        when(bookingRepository.findNextClosestBookingByOwnerId(anyLong(), anyLong()))
//                .thenReturn(nextBookingClosest);
//        when(bookingRepository.findLastClosestBookingByOwnerId(anyLong(), anyLong()))
//                .thenReturn(lastBookingClosest);
//
//        List<ItemDto> expectItemDto = List.of(ItemMapper.toItemDto(item));
//        expectItemDto.get(0).setComments(Collections.emptyList());
//        expectItemDto.get(0).setNextBooking(nextBookingClosest.get(0));
//        expectItemDto.get(0).setLastBooking(lastBookingClosest.get(0));
//        List<ItemDto> actualItemDto = itemService.getItemsByOwnerId(userId, from, size);
//        assertEquals(expectItemDto, actualItemDto);
//    }

    @Test
    void getItems_whenValid_thenReturnAllItems() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setId(itemId);
        item.setOwner(user);

        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(commentRepository.findAllByItemIdIn(List.of(itemId))).thenReturn(Collections.emptyList());

        List<ItemDto> expectItemDto = List.of(ItemMapper.toItemDto(item));
        expectItemDto.get(0).setComments(Collections.emptyList());
        List<ItemDto> actualItemDto = itemService.getItems();
        assertEquals(expectItemDto, actualItemDto);
    }

    @Test
    void updateItem_whenValid_thenReturnUpdatedItem() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        Item oldItem = new Item();
        long itemId = 1L;
        oldItem.setId(itemId);
        oldItem.setOwner(user);

        Item newItem = new Item();
        newItem.setId(itemId);
        newItem.setOwner(user);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(newItem));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(Collections.emptyList());

        ItemDto expectItem = ItemMapper.toItemDto(newItem);
        expectItem.setComments(Collections.emptyList());
        ItemDto actualItem = itemService.updateItem(itemId, userId, ItemMapper.toItemDto(newItem));
        assertEquals(expectItem, actualItem);
    }

    @Test
    void searchItems_whenValid_thenReturnItemList() {
        String text1 = "";
        String text2 = "Name1";
        String text3 = "Description";

        User user = new User();
        long userId = 1L;
        user.setId(userId);

        Item item1 = new Item();
        long itemId1 = 1L;
        item1.setId(itemId1);
        item1.setOwner(user);

        Item item2 = new Item();
        long itemId2 = 2L;
        item2.setId(itemId2);
        item2.setOwner(user);

        int from = 0, size = 10;
        Pageable page = PageRequest.of(from / size, size);

        when(itemRepository.searchItemByNameOrDescription(text2, page))
                .thenReturn(List.of(item1, item2));
        when(itemRepository.searchItemByNameOrDescription(text3, page))
                .thenReturn(List.of(item1, item2));

        List<ItemDto> expectedList1 = Collections.emptyList();
        assertEquals(expectedList1, itemService.searchItems(text1, from, size));

        List<ItemDto> expectedList2 = ItemMapper.toItemDto(List.of(item1, item2));
        expectedList2.get(0).setComments(Collections.emptyList());
        expectedList2.get(1).setComments(Collections.emptyList());
        assertEquals(expectedList2, itemService.searchItems(text2, from, size));

        List<ItemDto> expectedList3 = ItemMapper.toItemDto(List.of(item1, item2));
        expectedList3.get(0).setComments(Collections.emptyList());
        expectedList3.get(1).setComments(Collections.emptyList());
        assertEquals(expectedList3, itemService.searchItems(text3, from, size));
    }

    @Test
    void deleteItem_whenExecuted_thenItemIsRemovedFromDb() {
        long itemId = 1L;
        itemService.deleteItem(itemId);
    }

    @Test
    void addComment_whenValid_thenReturnComment() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setId(itemId);
        item.setOwner(user);

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("comment");
        Comment comment = CommentMapper.fromCommentRequestDto(commentRequestDto);
        comment.setCreated(ZonedDateTime.now(ZoneId.of("Europe/Moscow")).plusMinutes(1));
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setId(1L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setEnd(LocalDateTime.MIN);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdPast(userId, PageRequest.of(0, 10)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentResponseDto expect = CommentMapper.toCommentResponseDto(comment);
        CommentResponseDto actual = itemService.addComment(commentRequestDto, userId, itemId);
        assertEquals(expect, actual);
    }

    @Test
    void addComment_whenNotAuthorised_thenCommentNotAuthorisedException() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setId(itemId);
        item.setOwner(user);

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("comment");
        Comment comment = CommentMapper.fromCommentRequestDto(commentRequestDto);
        comment.setCreated(ZonedDateTime.now(ZoneId.of("Europe/Moscow")).plusMinutes(1));
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setId(1L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setEnd(LocalDateTime.MIN);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdPast(userId, PageRequest.of(0, 10)))
                .thenReturn(Collections.emptyList());

        assertThrows(
                CommentNotAuthorisedException.class,
                () -> itemService.addComment(
                                commentRequestDto,
                                userId,
                                itemId)
        );
    }

    @Test
    void addComment_whenCommentCreatedAfterBookingEnd_thenCommentNotAuthorisedException() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setId(itemId);
        item.setOwner(user);

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("comment");
        Comment comment = CommentMapper.fromCommentRequestDto(commentRequestDto);
        comment.setCreated(ZonedDateTime.now(ZoneId.of("Europe/Moscow")).plusMinutes(1));
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setId(1L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setEnd(LocalDateTime.MAX);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdPast(userId, PageRequest.of(0, 10)))
                .thenReturn(List.of(booking));

        assertThrows(
                CommentNotAuthorisedException.class,
                () -> itemService.addComment(
                                commentRequestDto,
                                userId,
                                itemId)
        );
    }

    @Test
    void addComment_whenCommentTextIsEmpty_thenCommentNotAuthorisedException() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setId(itemId);
        item.setOwner(user);

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("");
        Comment comment = CommentMapper.fromCommentRequestDto(commentRequestDto);
        comment.setCreated(ZonedDateTime.now(ZoneId.of("Europe/Moscow")).plusMinutes(1));
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setId(1L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setEnd(LocalDateTime.MIN);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdPast(userId, PageRequest.of(0, 10)))
                .thenReturn(List.of(booking));

        assertThrows(
                CommentNotAuthorisedException.class,
                () -> itemService.addComment(
                                commentRequestDto,
                                userId,
                                itemId)
        );
    }

}