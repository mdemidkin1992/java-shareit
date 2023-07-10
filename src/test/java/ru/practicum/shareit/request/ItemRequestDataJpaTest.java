package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestDataJpaTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private static final int FROM = 0;
    private static final int SIZE = 10;

    private long requesterId;

    private long itemRequestId1, itemRequestId2, itemRequestId3;

    private static final Pageable PAGE = PageRequest.of(FROM / SIZE, SIZE, Sort.by("created").descending());

    @BeforeEach
    private void init() {
        User requester = new User();
        requester.setName("requester");
        requester.setEmail("requester@email.com");
        requesterId = userRepository.save(requester).getId();

        User otherRequester = new User();
        otherRequester.setName("otherRequester");
        otherRequester.setEmail("otherRequester@email.com");
        userRepository.save(otherRequester).getId();

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setRequester(requester);
        itemRequest1.setDescription("description");
        itemRequest1.setCreated(ZonedDateTime.now());

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setRequester(requester);
        itemRequest2.setDescription("description");
        itemRequest2.setCreated(ZonedDateTime.now());

        ItemRequest itemRequest3 = new ItemRequest();
        itemRequest3.setRequester(otherRequester);
        itemRequest3.setDescription("description");
        itemRequest3.setCreated(ZonedDateTime.now());

        itemRequest1 = itemRequestRepository.save(itemRequest1);
        itemRequest2 = itemRequestRepository.save(itemRequest2);
        itemRequest3 = itemRequestRepository.save(itemRequest3);

        itemRequestId1 = itemRequest1.getId();
        itemRequestId2 = itemRequest2.getId();
        itemRequestId3 = itemRequest3.getId();
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDesc() {
        List<ItemRequest> expected = List.of(
                itemRequestRepository.findById(itemRequestId2).get(),
                itemRequestRepository.findById(itemRequestId1).get()
        );

        List<ItemRequest> actual = itemRequestRepository
                .findAllByRequesterIdOrderByCreatedDesc(requesterId);

        assertEquals(expected, actual);
    }

    @Test
    void findByRequesterIdIsNot() {
        List<ItemRequest> expected = List.of(
                itemRequestRepository.findById(itemRequestId3).get()
        );

        Page<ItemRequest> actual = itemRequestRepository
                .findByRequesterIdIsNot(requesterId, PAGE);

        assertEquals(expected, actual.toList());
    }

    @AfterEach
    private void delete() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}