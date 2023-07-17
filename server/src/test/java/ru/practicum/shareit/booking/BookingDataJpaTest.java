package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingClosest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingDataJpaTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private long bookerId;
    private long ownerId;
    private long itemId;

    private static final int FROM = 0;
    private static final int SIZE = 10;
    private static final LocalDateTime CURRENT_TIMESTAMP = LocalDateTime.now();

    private long bookingId1, bookingId2, bookingId3;

    private static final Pageable PAGE = PageRequest.of(FROM / SIZE, SIZE, Sort.by("start").descending());

    @BeforeEach
    void init() {
        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@email.com");
        ownerId = userRepository.save(owner).getId();

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@email.com");
        bookerId = userRepository.save(booker).getId();

        Item item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
        itemId = item.getId();

        Booking booking1 = new Booking();
        booking1.setItem(item);
        booking1.setBooker(booker);
        booking1.setStatus(StatusType.WAITING);
        booking1.setStart(LocalDateTime.of(2023, 10, 1, 9, 0, 30));
        booking1.setEnd(LocalDateTime.of(2023, 10, 2, 9, 0, 30));

        Booking booking2 = new Booking();
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(StatusType.WAITING);
        booking2.setStart(LocalDateTime.of(2023, 10, 1, 9, 0, 30));
        booking2.setEnd(LocalDateTime.of(2023, 10, 2, 9, 0, 30));

        Booking booking3 = new Booking();
        booking3.setItem(item);
        booking3.setBooker(booker);
        booking3.setStatus(StatusType.WAITING);
        booking3.setStart(LocalDateTime.of(2023, 10, 1, 9, 0, 30));
        booking3.setEnd(LocalDateTime.of(2023, 10, 2, 9, 0, 30));

        booking1 = bookingRepository.save(booking1);
        booking2 = bookingRepository.save(booking2);
        booking3 = bookingRepository.save(booking3);

        bookingId1 = booking1.getId();
        bookingId2 = booking2.getId();
        bookingId3 = booking3.getId();

        bookingRepository.updateBookingStatusById(bookingId1, true);
        bookingRepository.updateBookingStatusById(bookingId2, true);
        bookingRepository.updateBookingStatusById(bookingId3, false);
    }

    @Test
    @SneakyThrows
    void findAllByBookerIdOrderByStartDesc() {
        List<Booking> actual = bookingRepository
                .findAllByBookerIdOrderByStartDesc(bookerId, PAGE);
        List<Booking> expect = new ArrayList<>();
        expect.add(bookingRepository.findById(bookingId1).get());
        expect.add(bookingRepository.findById(bookingId2).get());
        expect.add(bookingRepository.findById(bookingId3).get());
        assertEquals(expect, actual);
    }

    @Test
    @SneakyThrows
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> actual = bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDesc(
                                bookerId,
                                StatusType.REJECTED,
                                PAGE
                        );
        List<Booking> expect = new ArrayList<>();
        expect.add(bookingRepository.findById(bookingId3).get());
        assertEquals(expect, actual);
    }

    @Test
    @SneakyThrows
    void findAllByBookerIdCurrent() {
        List<Booking> actual = bookingRepository
                .findAllByBookerIdCurrent(bookerId, PAGE);
        List<Booking> expect = Collections.emptyList();
        assertEquals(expect, actual);
    }

    @Test
    @SneakyThrows
    void findAllByBookerIdPast() {
        List<Booking> actual = bookingRepository
                .findAllByBookerIdPast(bookerId, PAGE);
        List<Booking> expect = Collections.emptyList();
        assertEquals(expect, actual);
    }

    @Test
    @SneakyThrows
    void findAllByBookerIdFuture() {
        List<Booking> actual = bookingRepository
                .findAllByBookerIdFuture(bookerId, PAGE);
        List<Booking> expect = new ArrayList<>();
        expect.add(bookingRepository.findById(bookingId1).get());
        expect.add(bookingRepository.findById(bookingId2).get());
        expect.add(bookingRepository.findById(bookingId3).get());
        assertEquals(expect, actual);
    }

    @Test
    @SneakyThrows
    void findAllByItemOwnerIdOrderByStartDesc() {
        List<Booking> actual = bookingRepository
                .findAllByItemOwnerIdOrderByStartDesc(ownerId, PAGE);
        List<Booking> expect = new ArrayList<>();
        expect.add(bookingRepository.findById(bookingId1).get());
        expect.add(bookingRepository.findById(bookingId2).get());
        expect.add(bookingRepository.findById(bookingId3).get());
        assertEquals(expect, actual);
    }

    @Test
    @SneakyThrows
    void findAllByItemOwnerIdCurrentOrderByStartDesc() {
        List<Booking> actual = bookingRepository
                .findAllByItemOwnerIdCurrentOrderByStartDesc(ownerId, PAGE);
        List<Booking> expect = Collections.emptyList();
        assertEquals(expect, actual);
    }

    @Test
    @SneakyThrows
    void findAllByItemOwnerIdPastOrderByStartDesc() {
        List<Booking> actual = bookingRepository
                .findAllByItemOwnerIdPastOrderByStartDesc(ownerId, PAGE);
        List<Booking> expect = Collections.emptyList();
        assertEquals(expect, actual);
    }

    @Test
    @SneakyThrows
    void findAllByItemOwnerIdFutureOrderByStartDesc() {
        List<Booking> actual = bookingRepository
                .findAllByItemOwnerIdFutureOrderByStartDesc(ownerId, PAGE);
        List<Booking> expect = new ArrayList<>();
        expect.add(bookingRepository.findById(bookingId1).get());
        expect.add(bookingRepository.findById(bookingId2).get());
        expect.add(bookingRepository.findById(bookingId3).get());
        assertEquals(expect, actual);
    }

    @Test
    @SneakyThrows
    void findAllByItemOwnerIdAndStatusOrderByStartDesc() {
        List<Booking> actual = bookingRepository
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(
                                ownerId,
                                StatusType.REJECTED,
                                PAGE
                        );
        List<Booking> expect = new ArrayList<>();
        expect.add(bookingRepository.findById(bookingId3).get());
        assertEquals(expect, actual);
    }

    @Test
    @SneakyThrows
    void findNextClosestBookingByOwnerId() {
        Booking nextBooking = bookingRepository
                .findBookingsByItemOwnerIdAndStatusAndItemOrderByStartDesc(ownerId, itemId)
                .stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList())
                .get(0);

        BookingClosest nextBookingClosest = new BookingClosest(nextBooking.getId(), nextBooking.getBooker().getId());

        assertEquals(nextBookingClosest.getBookerId(), bookerId);
    }

    @Test
    @SneakyThrows
    void findLastClosestBookingByOwnerId() {
        List<Booking> last = bookingRepository
                .findBookingsByItemOwnerIdAndStatusAndItemOrderByStartAsc(ownerId, itemId)
                .stream()
                .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        assertEquals(last.size(), 0);
    }

    @AfterEach
    private void delete() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

}