package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemDataJpaTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldUpdateItem() {

    }

    @Test
    public void shouldSearchForItems() {
        UserDto userDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        User owner = UserMapper.fromUserDto(userDto);
        owner = userRepository.save(owner);

        Item item1 = ItemMapper.fromItemDto(ItemDto.builder().name("Item1").description("Description1").available(true).build());
        Item item2 = ItemMapper.fromItemDto(ItemDto.builder().name("Item2").description("Description2").available(true).build());

        item1.setOwner(owner);
        item2.setOwner(owner);

        itemRepository.save(item1);
        itemRepository.save(item2);

        String searchText = "item";

        List<Item> expectedList = new ArrayList<>();
        expectedList.add(item1);
        expectedList.add(item2);

        assertEquals(expectedList, itemRepository.searchItemByNameOrDescription(searchText, PageRequest.of(0, 2)));
    }

    @AfterEach
    private void delete() {
        itemRepository.deleteAll();
    }

}