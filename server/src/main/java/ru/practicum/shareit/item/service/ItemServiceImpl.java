package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.CommentNotAuthorisedException;
import ru.practicum.shareit.util.exception.ItemNotFoundException;
import ru.practicum.shareit.util.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto createItem(long ownerId, ItemDto itemDto) {
        User user = checkPresenceAndReturnUserOrElseThrow(ownerId);
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(user);
        Long requestId = itemDto.getRequestId();
        if (requestId != null) {
            Optional<ItemRequest> maybeRequest = itemRequestRepository.findById(requestId);
            if (maybeRequest.isPresent()) {
                ItemRequest request = maybeRequest.get();
                item.setRequest(request);
            } else {
                throw new ItemRequestNotFoundException("Item request " + requestId + " not found");
            }
        }
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(long itemId, long ownerId) {
        Item item = checkPresenceAndReturnItemOrElseThrow(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        itemDto.setComments(CommentMapper.toCommentResponseDto(comments));

        if (itemDto.getOwnerId() == ownerId) {

            List<BookingClosest> nextBookingClosest = bookingRepository
                    .findNextClosestBookingByOwnerId(
                            ownerId,
                            itemId
                    );

            List<BookingClosest> lastBookingClosest = bookingRepository
                    .findLastClosestBookingByOwnerId(
                            ownerId,
                            itemId
                    );

            if (!nextBookingClosest.isEmpty()) {
                itemDto.setNextBooking(nextBookingClosest.get(0));
            }

            if (!lastBookingClosest.isEmpty()) {
                itemDto.setLastBooking(lastBookingClosest.get(0));
            }
        }

        return itemDto;
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByOwnerId(long ownerId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(ownerId, page);
        List<Long> ids = getItemsIds(items);
        List<ItemDto> itemDtos = combineItemsWithComments(items, ids);

        for (ItemDto i : itemDtos) {

            List<BookingClosest> nextBookingClosest = bookingRepository
                    .findNextClosestBookingByOwnerId(
                            ownerId,
                            i.getId()
                    );

            List<BookingClosest> lastBookingClosest = bookingRepository
                    .findLastClosestBookingByOwnerId(
                            ownerId,
                            i.getId()
                    );

            if (!nextBookingClosest.isEmpty()) {
                i.setNextBooking(nextBookingClosest.get(0));
            }

            if (!lastBookingClosest.isEmpty()) {
                i.setLastBooking(lastBookingClosest.get(0));
            }


        }

        return itemDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItems() {
        List<Item> items = itemRepository.findAll();
        List<Long> ids = getItemsIds(items);
        return combineItemsWithComments(items, ids);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long itemId, long ownerId, ItemDto itemDto) {
        checkPresenceAndReturnUserOrElseThrow(ownerId);
        itemRepository.updateItemFields(ItemMapper.fromItemDto(itemDto), ownerId, itemId);
        ItemDto updatedItemDto = ItemMapper.toItemDto(checkPresenceAndReturnItemOrElseThrow(itemId));
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        updatedItemDto.setComments(CommentMapper.toCommentResponseDto(comments));
        return updatedItemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text, int from, int size) {
        if (text.isEmpty()) return new ArrayList<>();
        Pageable page = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.searchItemByNameOrDescription(text, page);
        List<Long> ids = getItemsIds(items);
        return combineItemsWithComments(items, ids);
    }

    @Override
    @Transactional
    public void deleteItem(long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(CommentRequestDto commentRequestDto, long bookerId, long itemId) {
        User user = checkPresenceAndReturnUserOrElseThrow(bookerId);
        Item item = checkPresenceAndReturnItemOrElseThrow(itemId);

        List<Booking> bookings = bookingRepository.findAllByBookerIdPast(bookerId, PageRequest.of(0, 10));

        if (bookings.isEmpty())
            throw new CommentNotAuthorisedException("Booking from user " + bookerId + " for item " + itemId + " doesn't exist");

        Booking booking = new Booking();
        for (Booking b : bookings) {
            if (b.getItem().getId() == itemId) {
                booking = b;
            }
        }

        Comment comment = CommentMapper.fromCommentRequestDto(commentRequestDto);

        ZoneId zoneId = ZoneId.of("Europe/Moscow");
        ZonedDateTime moscowDateTime = ZonedDateTime.now(zoneId);
        comment.setCreated(moscowDateTime.plusMinutes(1));

        if (ZonedDateTime.of(booking.getEnd(), zoneId).isAfter(comment.getCreated())) {
            throw new CommentNotAuthorisedException("Comment field created must be after booking end");
        }
        if (comment.getText().isEmpty()) {
            throw new CommentNotAuthorisedException("Comment text should not be empty");
        }

        comment.setItem(item);
        comment.setAuthor(user);
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentResponseDto(comment);
    }

    private List<Long> getItemsIds(List<Item> items) {
        return items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
    }

    private List<ItemDto> combineItemsWithComments(List<Item> items, List<Long> ids) {
        List<Comment> comments = commentRepository.findAllByItemIdIn(ids);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item i : items) {
            List<CommentResponseDto> itemComments = comments.stream()
                    .filter(c -> c.getItem().getId() == i.getId())
                    .map(CommentMapper::toCommentResponseDto)
                    .collect(Collectors.toList());
            ItemDto dto = ItemMapper.toItemDto(i);
            dto.setComments(itemComments);
            itemDtos.add(dto);
        }
        return itemDtos;
    }

    private User checkPresenceAndReturnUserOrElseThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
    }

    private Item checkPresenceAndReturnItemOrElseThrow(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(("Item with id " + itemId + " not found")));
    }

}
