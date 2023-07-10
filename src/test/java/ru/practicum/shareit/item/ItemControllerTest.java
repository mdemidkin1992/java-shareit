package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.CrudOperations;
import ru.practicum.shareit.util.exception.ItemNotFoundException;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemControllerTest extends CrudOperations {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void shouldGetItemWhenIdIsCorrect() throws Exception {
        UserDto userDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(userDto).getId();
        ItemDto request = ItemDto.builder().name("Item").description("Description").available(true).build();
        ItemDto response = createItem(request, ownerId);

        mockMvc.perform(get("/items/{itemId}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.description").value(response.getDescription()))
                .andExpect(jsonPath("$.available").value(response.getAvailable()))
                .andReturn();
    }

    @Test
    public void shouldThrowItemNotFoundExceptionWhenIdIsIncorrect() throws Exception {
        long ownerId = createUser(UserDto.builder().name("Mark").email("mark@email.com").build()).getId();
        createItem(ItemDto.builder().name("Item").description("Description").available(true).build(), ownerId);
        long invalidId = 999;

        mockMvc.perform(get("/items/{itemId}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof ItemNotFoundException));
    }

    @Test
    public void shouldGetAllItems() throws Exception {
        long ownerId = createUser(UserDto.builder().name("Mark").email("mark@email.com").build()).getId();
        createItem(ItemDto.builder().name("Item1").description("Description1").available(true).build(), ownerId);
        createItem(ItemDto.builder().name("Item2").description("Description2").available(true).build(), ownerId);

        mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andReturn();
    }

    @Test
    public void shouldUpdateItemWhenValid() throws Exception {
        long ownerId = createUser(UserDto.builder().name("Mark").email("mark@email.com").build()).getId();
        ItemDto itemDto = createItem(ItemDto.builder().name("Item1").description("Description1").available(true).build(), ownerId);

        Map<String, String> fields = new HashMap<>();
        fields.put("name", "New name");
        fields.put("description", "newDescription");
        fields.put("available", "false");

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fields))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(fields.get("name")))
                .andExpect(jsonPath("$.description").value(fields.get("description")))
                .andExpect(jsonPath("$.available").value(fields.get("available")))
                .andReturn();
    }

    @Test
    public void shouldThrowItemNotFoundExceptionWhenItemIdInvalid() throws Exception {
        long ownerId = createUser(UserDto.builder().name("Mark").email("mark@email.com").build()).getId();
        createItem(ItemDto.builder().name("Item").description("Description").available(true).build(), ownerId);
        long itemInvalidId = 999;

        Map<String, String> fields = new HashMap<>();
        fields.put("name", "New name");
        fields.put("description", "newDescription");
        fields.put("available", "false");

        mockMvc.perform(patch("/items/{itemId}", itemInvalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fields))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof ItemNotFoundException));
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenUserIdInvalid() throws Exception {
        long ownerId = createUser(UserDto.builder().name("Mark").email("mark@email.com").build()).getId();
        long itemId = createItem(ItemDto.builder().name("Item").description("Description").available(true).build(), ownerId).getId();
        long ownerInvalidId = 999;

        Map<String, String> fields = new HashMap<>();
        fields.put("name", "New name");
        fields.put("description", "newDescription");
        fields.put("available", "false");

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fields))
                        .header("X-Sharer-User-Id", String.valueOf(ownerInvalidId)))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserNotFoundException));
    }

    @Test
    public void shouldGetSearchResultsWhenQueryNonEmpty() throws Exception {
        long ownerId = createUser(UserDto.builder().name("Mark").email("mark@email.com").build()).getId();
        createItem(ItemDto.builder().name("Item1").description("Description1").available(true).build(), ownerId);
        createItem(ItemDto.builder().name("Item2").description("Description2").available(true).build(), ownerId);

        String text = "item";

        mockMvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", text))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andReturn();
    }

    @Test
    public void shouldGetNoSearchResultsWhenQueryIsEmpty() throws Exception {
        long ownerId = createUser(UserDto.builder().name("Mark").email("mark@email.com").build()).getId();
        createItem(ItemDto.builder().name("Item1").description("Description1").available(true).build(), ownerId);
        createItem(ItemDto.builder().name("Item2").description("Description2").available(true).build(), ownerId);

        String text = "";

        mockMvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", text))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)))
                .andReturn();
    }

    @Test
    public void shouldDeleteItemWhenIdCorrect() throws Exception {
        long ownerId = createUser(UserDto.builder().name("Mark").email("mark@email.com").build()).getId();
        long itemId = createItem(ItemDto.builder().name("Item1").description("Description1").available(true).build(), ownerId).getId();

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)))
                .andReturn();
    }

    @Test
    @SneakyThrows
    public void shouldAddComment() {
        long ownerId = createUser(UserDto.builder().name("Mark").email("mark@email.com").build()).getId();
        long bookerId = createUser(UserDto.builder().name("Toma").email("toma@email.com").build()).getId();
        long itemId = createItem(ItemDto.builder().name("Item1").description("Description1").available(true).build(), ownerId).getId();

        Optional<Item> item = itemRepository.findById(itemId);
        Optional<User> booker = userRepository.findById(bookerId);

        Booking booking = new Booking();
        booking.setItem(item.get());
        booking.setBooker(booker.get());
        booking.setStatus(StatusType.WAITING);
        booking.setStart(LocalDateTime.of(2023, 6, 1, 9, 0, 30));
        booking.setEnd(LocalDateTime.of(2023, 6, 2, 9, 0, 30));

        bookingRepository.save(booking);

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("comment");

        MvcResult result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", String.valueOf(bookerId))
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        CommentResponseDto commentResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CommentResponseDto.class
        );

        assertEquals(commentResponseDto.getId(), 1L);
        assertEquals(commentResponseDto.getText(), commentRequestDto.getText());
        assertEquals(commentResponseDto.getAuthorName(), booking.getBooker().getName());
    }

}