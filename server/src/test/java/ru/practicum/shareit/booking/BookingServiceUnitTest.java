package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.AccessDenyException;
import ru.practicum.shareit.util.exception.BookingNotFoundException;
import ru.practicum.shareit.util.exception.ItemNotAvailbaleException;
import ru.practicum.shareit.util.exception.UnsupportedStateException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @Test
    void createBooking_whenValid_thenReturnBooking() {
        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long userId = 2L;
        booker.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoRequest request = new BookingDtoRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking = BookingMapper.fromBookingDtoRequest(request, booker, item);
        booking.setStatus(BookingState.WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDtoResponse expect = BookingMapper.toBookingDto(booking);
        BookingDtoResponse actual = bookingService.createBooking(request, userId);
        assertEquals(expect, actual);
    }

    @Test
    void createBooking_whenItemNotAvailable_thenItemNotAvailableException() {
        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long userId = 2L;
        booker.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(false);

        BookingDtoRequest request = new BookingDtoRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking = BookingMapper.fromBookingDtoRequest(request, booker, item);
        booking.setStatus(BookingState.WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(
                ItemNotAvailbaleException.class,
                () -> bookingService.createBooking(request, userId)
        );
    }

    @Test
    void createBooking_whenBookerIdEqualsOwnerId_thenBookingNotFoundException() {
        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long userId = 1L;
        booker.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoRequest request = new BookingDtoRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking = BookingMapper.fromBookingDtoRequest(request, booker, item);
        booking.setStatus(BookingState.WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.createBooking(request, userId)
        );
    }

    @Test
    void updateBooking_whenValid_thenReturnBooking() {
        Boolean approved = false;

        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long userId = 2L;
        booker.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoRequest request = new BookingDtoRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking = BookingMapper.fromBookingDtoRequest(request, booker, item);
        long bookingId = 1L;
        booking.setId(bookingId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDtoResponse expect = BookingMapper.toBookingDto(booking);
        expect.setStatus("REJECTED");
        BookingDtoResponse actual = bookingService.updateBooking(bookingId, approved, ownerId);
        assertEquals(expect, actual);
    }

    @Test
    void updateBooking_whenUserIsNotOwner_thenAccessDenyException() {
        Boolean approved = true;

        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long userId = 2L;
        booker.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoRequest request = new BookingDtoRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking = BookingMapper.fromBookingDtoRequest(request, booker, item);
        long bookingId = 1L;
        booking.setId(bookingId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(AccessDenyException.class,
                () -> bookingService.updateBooking(bookingId, approved, userId)
        );
    }

    @Test
    void updateBooking_whenStatusAlreadyChanged_thenUnsupportedStateException() {
        Boolean approved = true;

        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long userId = 2L;
        booker.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoRequest request = new BookingDtoRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking = BookingMapper.fromBookingDtoRequest(request, booker, item);
        long bookingId = 1L;
        booking.setId(bookingId);
        booking.setStatus(BookingState.APPROVED);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(UnsupportedStateException.class,
                () -> bookingService.updateBooking(bookingId, approved, ownerId)
        );
    }

    @Test
    void getBookingsByUserByState_whenValid_thenReturnBookingsList() {
        int from = 0, size = 10;
        Pageable page = PageRequest.of(from / size, size, Sort.by("start").descending());

        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long bookerId = 2L;
        booker.setId(bookerId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoRequest request1 = new BookingDtoRequest();
        request1.setItemId(itemId);
        request1.setStart(LocalDateTime.now());
        request1.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking1 = BookingMapper.fromBookingDtoRequest(request1, booker, item);
        long bookingId1 = 1L;
        booking1.setId(bookingId1);
        booking1.setStatus(BookingState.APPROVED);

        BookingDtoRequest request2 = new BookingDtoRequest();
        request2.setItemId(itemId);
        request2.setStart(LocalDateTime.now());
        request2.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking2 = BookingMapper.fromBookingDtoRequest(request2, booker, item);
        long bookingId2 = 2L;
        booking2.setId(bookingId2);
        booking2.setStatus(BookingState.REJECTED);

        BookingDtoRequest request3 = new BookingDtoRequest();
        request3.setItemId(itemId);
        request3.setStart(LocalDateTime.now());
        request3.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking3 = BookingMapper.fromBookingDtoRequest(request3, booker, item);
        long bookingId3 = 3L;
        booking3.setId(bookingId3);
        booking3.setStatus(BookingState.WAITING);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, page))
                .thenReturn(List.of(booking1, booking2, booking3));
        when(bookingRepository.findAllByBookerIdCurrent(bookerId, page))
                .thenReturn(List.of(booking1, booking2, booking3));
        when(bookingRepository.findAllByBookerIdPast(bookerId, page))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByBookerIdFuture(bookerId, page))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingState.REJECTED, page))
                .thenReturn(List.of(booking3));

        List<BookingDtoResponse> expectedList1 = BookingMapper.toBookingDto(List.of(booking1, booking2, booking3));
        List<BookingDtoResponse> actualList1 = bookingService.getBookingsByUserByState("ALL", bookerId, from, size);
        assertEquals(expectedList1, actualList1);

        List<BookingDtoResponse> expectedList2 = BookingMapper.toBookingDto(List.of(booking1, booking2, booking3));
        List<BookingDtoResponse> actualList2 = bookingService.getBookingsByUserByState("CURRENT", bookerId, from, size);
        assertEquals(expectedList2, actualList2);

        List<BookingDtoResponse> expectedList3 = BookingMapper.toBookingDto(Collections.emptyList());
        List<BookingDtoResponse> actualList3 = bookingService.getBookingsByUserByState("FUTURE", bookerId, from, size);
        assertEquals(expectedList3, actualList3);

        List<BookingDtoResponse> expectedList4 = BookingMapper.toBookingDto(Collections.emptyList());
        List<BookingDtoResponse> actualList4 = bookingService.getBookingsByUserByState("PAST", bookerId, from, size);
        assertEquals(expectedList4, actualList4);

        List<BookingDtoResponse> expectedList5 = BookingMapper.toBookingDto(List.of(booking3));
        List<BookingDtoResponse> actualList5 = bookingService.getBookingsByUserByState("REJECTED", bookerId, from, size);
        assertEquals(expectedList5, actualList5);

        assertThrows(UnsupportedStateException.class,
                () -> bookingService.getBookingsByUserByState("UNSUPPORTED", bookerId, from, size));
    }

    @Test
    void getOwnerItemsBooked_whenValid_thenReturnBookingsList() {
        int from = 0, size = 10;
        Pageable page = PageRequest.of(from / size, size, Sort.by("start").descending());

        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long bookerId = 2L;
        booker.setId(bookerId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoRequest request1 = new BookingDtoRequest();
        request1.setItemId(itemId);
        request1.setStart(LocalDateTime.now());
        request1.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking1 = BookingMapper.fromBookingDtoRequest(request1, booker, item);
        long bookingId1 = 1L;
        booking1.setId(bookingId1);
        booking1.setStatus(BookingState.APPROVED);

        BookingDtoRequest request2 = new BookingDtoRequest();
        request2.setItemId(itemId);
        request2.setStart(LocalDateTime.now());
        request2.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking2 = BookingMapper.fromBookingDtoRequest(request2, booker, item);
        long bookingId2 = 2L;
        booking2.setId(bookingId2);
        booking2.setStatus(BookingState.REJECTED);

        BookingDtoRequest request3 = new BookingDtoRequest();
        request3.setItemId(itemId);
        request3.setStart(LocalDateTime.now());
        request3.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking3 = BookingMapper.fromBookingDtoRequest(request3, booker, item);
        long bookingId3 = 3L;
        booking3.setId(bookingId3);
        booking3.setStatus(BookingState.WAITING);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, page))
                .thenReturn(List.of(booking1, booking2, booking3));
        when(bookingRepository.findAllByItemOwnerIdCurrentOrderByStartDesc(ownerId, page))
                .thenReturn(List.of(booking1, booking2, booking3));
        when(bookingRepository.findAllByItemOwnerIdPastOrderByStartDesc(ownerId, page))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemOwnerIdFutureOrderByStartDesc(ownerId, page))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingState.REJECTED, page))
                .thenReturn(List.of(booking3));

        List<BookingDtoResponse> expectedList1 = BookingMapper.toBookingDto(List.of(booking1, booking2, booking3));
        List<BookingDtoResponse> actualList1 = bookingService.getOwnerItemsBooked("ALL", ownerId, from, size);
        assertEquals(expectedList1, actualList1);

        List<BookingDtoResponse> expectedList2 = BookingMapper.toBookingDto(List.of(booking1, booking2, booking3));
        List<BookingDtoResponse> actualList2 = bookingService.getOwnerItemsBooked("CURRENT", ownerId, from, size);
        assertEquals(expectedList2, actualList2);

        List<BookingDtoResponse> expectedList3 = BookingMapper.toBookingDto(Collections.emptyList());
        List<BookingDtoResponse> actualList3 = bookingService.getOwnerItemsBooked("FUTURE", ownerId, from, size);
        assertEquals(expectedList3, actualList3);

        List<BookingDtoResponse> expectedList4 = BookingMapper.toBookingDto(Collections.emptyList());
        List<BookingDtoResponse> actualList4 = bookingService.getOwnerItemsBooked("PAST", ownerId, from, size);
        assertEquals(expectedList4, actualList4);

        List<BookingDtoResponse> expectedList5 = BookingMapper.toBookingDto(List.of(booking3));
        List<BookingDtoResponse> actualList5 = bookingService.getOwnerItemsBooked("REJECTED", ownerId, from, size);
        assertEquals(expectedList5, actualList5);

        assertThrows(UnsupportedStateException.class,
                () -> bookingService.getOwnerItemsBooked("UNSUPPORTED", ownerId, from, size));
    }

    @Test
    void getBookingById_whenValid_thenReturnBooking() {
        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long userId = 2L;
        booker.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoRequest request = new BookingDtoRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking = BookingMapper.fromBookingDtoRequest(request, booker, item);
        long bookingId = 1L;
        booking.setId(bookingId);
        booking.setStatus(BookingState.WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDtoResponse expect = BookingMapper.toBookingDto(booking);
        BookingDtoResponse actual = bookingService.getBookingById(bookingId, userId);
        assertEquals(expect, actual);
    }

    @Test
    void getBookingById_whenBookedIdEqualOwnerId_thenAccessDenyException() {
        User owner = new User();
        long ownerId = 1L;
        owner.setId(ownerId);

        User booker = new User();
        long userId = 2L;
        booker.setId(userId);

        Item item = new Item();
        long itemId = 1L;
        item.setOwner(owner);
        item.setId(itemId);
        item.setAvailable(true);

        BookingDtoRequest request = new BookingDtoRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusHours(1));
        Booking booking = BookingMapper.fromBookingDtoRequest(request, booker, item);
        long bookingId = 1L;
        booking.setId(bookingId);
        booking.setStatus(BookingState.WAITING);
        booking.setBooker(owner);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(AccessDenyException.class,
                () -> bookingService.getBookingById(bookingId, userId));
    }
}