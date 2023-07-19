package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.CrudOperations;
import ru.practicum.shareit.util.exception.AccessDenyException;
import ru.practicum.shareit.util.exception.BookingNotFoundException;
import ru.practicum.shareit.util.exception.ItemNotAvailbaleException;
import ru.practicum.shareit.util.exception.UnsupportedStateException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerTest extends CrudOperations {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final Validator VALIDATOR;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void testValidations() {
        BookingDtoRequest invalidBooking = new BookingDtoRequest();
        invalidBooking.setStart(LocalDateTime.of(800, 1, 1, 12, 0, 0));
        invalidBooking.setEnd(LocalDateTime.of(900, 1, 1, 12, 0, 0));
        Set<ConstraintViolation<BookingDtoRequest>> validates = VALIDATOR.validate(invalidBooking);
        assertTrue(validates.size() > 0);
    }

    @Test
    void createBooking_whenBookingIdIncorrect_thenBookingNotFoundException() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(true).build();
        long itemId = createItem(itemDto, ownerId).getId();

        BookingDtoRequest request = new BookingDtoRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.of(2023, 10, 1, 9, 0, 30));
        request.setEnd(LocalDateTime.of(2023, 10, 2, 9, 0, 30));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof BookingNotFoundException));
    }

    @Test
    void createBooking_whenItemNotAvailable_thenItemNotAvailableException() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(false).build();
        long itemId = createItem(itemDto, ownerId).getId();

        BookingDtoRequest request = new BookingDtoRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.of(2023, 10, 1, 9, 0, 30));
        request.setEnd(LocalDateTime.of(2023, 10, 2, 9, 0, 30));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof ItemNotAvailbaleException));
    }

    @Test
    void getBooking_whenValid_thenReturnBooking() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        UserDto bookerDto = UserDto.builder().name("Toma").email("toma@email.com").build();
        long bookerId = createUser(bookerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(true).build();
        long itemId = createItem(itemDto, ownerId).getId();

        BookingDtoRequest request = new BookingDtoRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.of(2023, 10, 1, 9, 0, 30));
        request.setEnd(LocalDateTime.of(2023, 10, 2, 9, 0, 30));

        BookingDtoResponse response = createBooking(request, bookerId);

        mockMvc.perform(get("/bookings/{bookingId}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(bookerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.start").value(response.getStart()))
                .andExpect(jsonPath("$.end").value(response.getEnd()))
                .andExpect(jsonPath("$.booker.id").value(response.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(response.getItem().getId()))
                .andReturn();
    }

    @Test
    void getBooking_whenOtherUser_thenAccessDenyException() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        UserDto bookerDto = UserDto.builder().name("Toma").email("toma@email.com").build();
        long bookerId = createUser(bookerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(true).build();
        long itemId = createItem(itemDto, ownerId).getId();

        long otherId = createUser(UserDto.builder().name("otherName").email("other@email.com").build()).getId();

        BookingDtoRequest request = new BookingDtoRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.of(2023, 10, 1, 9, 0, 30));
        request.setEnd(LocalDateTime.of(2023, 10, 2, 9, 0, 30));

        BookingDtoResponse response = createBooking(request, bookerId);

        mockMvc.perform(get("/bookings/{bookingId}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(otherId)))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof AccessDenyException));
    }

    @Test
    void updateBooking_whenValid_thenReturnUpdatedBooking() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        UserDto bookerDto = UserDto.builder().name("Toma").email("toma@email.com").build();
        long bookerId = createUser(bookerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(true).build();
        long itemId = createItem(itemDto, ownerId).getId();

        BookingDtoRequest request = new BookingDtoRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.of(2023, 10, 1, 9, 0, 30));
        request.setEnd(LocalDateTime.of(2023, 10, 2, 9, 0, 30));

        BookingDtoResponse response = createBooking(request, bookerId);

        Boolean approved = false;

        mockMvc.perform(patch("/bookings/{bookingId}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(BookingState.REJECTED)))
                .andReturn();

        mockMvc.perform(patch("/bookings/{bookingId}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UnsupportedStateException));
    }

    @Test
    void updateBooking_whenOtherUser_thenAccessDenyException() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        UserDto bookerDto = UserDto.builder().name("Toma").email("toma@email.com").build();
        long bookerId = createUser(bookerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(true).build();
        long itemId = createItem(itemDto, ownerId).getId();

        BookingDtoRequest request = new BookingDtoRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.of(2023, 10, 1, 9, 0, 30));
        request.setEnd(LocalDateTime.of(2023, 10, 2, 9, 0, 30));

        BookingDtoResponse response = createBooking(request, bookerId);

        Boolean approved = false;

        long otherId = createUser(UserDto.builder().name("otherName").email("other@email.com").build()).getId();

        mockMvc.perform(patch("/bookings/{bookingId}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved))
                        .header("X-Sharer-User-Id", String.valueOf(otherId)))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof AccessDenyException));
    }

    @Test
    void getBookingsByUserByState_whenValid_thenReturnBookingsList() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        UserDto bookerDto = UserDto.builder().name("Toma").email("toma@email.com").build();
        long bookerId = createUser(bookerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(true).build();
        long itemId = createItem(itemDto, ownerId).getId();

        BookingDtoRequest request1 = new BookingDtoRequest();
        request1.setItemId(itemId);
        request1.setStart(LocalDateTime.of(2023, 10, 1, 9, 0, 30));
        request1.setEnd(LocalDateTime.of(2023, 10, 2, 9, 0, 30));

        BookingDtoRequest request2 = new BookingDtoRequest();
        request2.setItemId(itemId);
        request2.setStart(LocalDateTime.of(2023, 10, 1, 9, 0, 30));
        request2.setEnd(LocalDateTime.of(2023, 10, 2, 9, 0, 30));

        BookingDtoRequest request3 = new BookingDtoRequest();
        request3.setItemId(itemId);
        request3.setStart(LocalDateTime.of(2023, 10, 1, 9, 0, 30));
        request3.setEnd(LocalDateTime.of(2023, 10, 2, 9, 0, 30));

        BookingDtoResponse response1 = createBooking(request1, bookerId);
        BookingDtoResponse response2 = createBooking(request2, bookerId);
        BookingDtoResponse response3 = createBooking(request3, bookerId);

        Boolean approved1 = true;
        Boolean approved2 = false;
        Boolean approved3 = false;

        mockMvc.perform(patch("/bookings/{bookingId}", response1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved1))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response1.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(BookingState.APPROVED)))
                .andReturn();

        response1.setStatus("APPROVED");

        mockMvc.perform(patch("/bookings/{bookingId}", response2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved2))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response2.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(BookingState.REJECTED)))
                .andReturn();

        response2.setStatus("REJECTED");

        mockMvc.perform(patch("/bookings/{bookingId}", response3.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved3))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response3.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(BookingState.REJECTED)))
                .andReturn();

        response3.setStatus("REJECTED");

        List<BookingDtoResponse> expectedList1 = new ArrayList<>();
        expectedList1.add(response1);
        expectedList1.add(response2);
        expectedList1.add(response3);

        List<BookingDtoResponse> expectedList2 = Collections.emptyList();

        List<BookingDtoResponse> expectedList3 = Collections.emptyList();

        List<BookingDtoResponse> expectedList4 = new ArrayList<>(expectedList1);

        List<BookingDtoResponse> expectedList5 = new ArrayList<>();
        expectedList5.add(response2);
        expectedList5.add(response3);

        int from = 0, size = 10;

        MvcResult result1 = this.mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", String.valueOf(bookerId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResponse> actualList1 = List.of(objectMapper.readValue(
                result1.getResponse().getContentAsString(),
                BookingDtoResponse[].class
        ));

        MvcResult result2 = this.mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "CURRENT")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", String.valueOf(bookerId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResponse> actualList2 = List.of(objectMapper.readValue(
                result2.getResponse().getContentAsString(),
                BookingDtoResponse[].class
        ));

        MvcResult result3 = this.mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "PAST")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", String.valueOf(bookerId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResponse> actualList3 = List.of(objectMapper.readValue(
                result3.getResponse().getContentAsString(),
                BookingDtoResponse[].class
        ));

        MvcResult result4 = this.mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "FUTURE")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", String.valueOf(bookerId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResponse> actualList4 = List.of(objectMapper.readValue(
                result4.getResponse().getContentAsString(),
                BookingDtoResponse[].class
        ));

        MvcResult result5 = this.mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "REJECTED")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", String.valueOf(bookerId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResponse> actualList5 = List.of(objectMapper.readValue(
                result5.getResponse().getContentAsString(),
                BookingDtoResponse[].class
        ));

        assertEquals(expectedList1, actualList1);
        assertEquals(expectedList2, actualList2);
        assertEquals(expectedList3, actualList3);
        assertEquals(expectedList4, actualList4);
        assertEquals(expectedList5, actualList5);

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "UNSUPPORTED")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", String.valueOf(bookerId))
                )
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UnsupportedStateException));
    }

    @Test
    void getOwnerItemsBooked_whenValid_theReturnBookingsList() throws Exception {
        UserDto ownerDto = UserDto.builder().name("Mark").email("mark@email.com").build();
        long ownerId = createUser(ownerDto).getId();

        UserDto bookerDto = UserDto.builder().name("Toma").email("toma@email.com").build();
        long bookerId = createUser(bookerDto).getId();

        ItemDto itemDto = ItemDto.builder().name("Item").description("Description").available(true).build();
        long itemId = createItem(itemDto, ownerId).getId();

        BookingDtoRequest request1 = new BookingDtoRequest();
        request1.setItemId(itemId);
        request1.setStart(LocalDateTime.of(2023, 10, 1, 9, 0, 30));
        request1.setEnd(LocalDateTime.of(2023, 10, 2, 9, 0, 30));

        BookingDtoRequest request2 = new BookingDtoRequest();
        request2.setItemId(itemId);
        request2.setStart(LocalDateTime.of(2023, 10, 1, 9, 0, 30));
        request2.setEnd(LocalDateTime.of(2023, 10, 2, 9, 0, 30));

        BookingDtoRequest request3 = new BookingDtoRequest();
        request3.setItemId(itemId);
        request3.setStart(LocalDateTime.of(2023, 10, 1, 9, 0, 30));
        request3.setEnd(LocalDateTime.of(2023, 10, 2, 9, 0, 30));

        BookingDtoResponse response1 = createBooking(request1, bookerId);
        BookingDtoResponse response2 = createBooking(request2, bookerId);
        BookingDtoResponse response3 = createBooking(request3, bookerId);

        Boolean approved1 = true;
        Boolean approved2 = false;
        Boolean approved3 = false;

        mockMvc.perform(patch("/bookings/{bookingId}", response1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved1))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response1.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(BookingState.APPROVED)))
                .andReturn();

        response1.setStatus("APPROVED");

        mockMvc.perform(patch("/bookings/{bookingId}", response2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved2))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response2.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(BookingState.REJECTED)))
                .andReturn();

        response2.setStatus("REJECTED");

        mockMvc.perform(patch("/bookings/{bookingId}", response3.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approved3))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response3.getId()))
                .andExpect(jsonPath("$.status").value(String.valueOf(BookingState.REJECTED)))
                .andReturn();

        response3.setStatus("REJECTED");

        List<BookingDtoResponse> expectedList1 = new ArrayList<>();
        expectedList1.add(response1);
        expectedList1.add(response2);
        expectedList1.add(response3);

        List<BookingDtoResponse> expectedList2 = Collections.emptyList();

        List<BookingDtoResponse> expectedList3 = Collections.emptyList();

        List<BookingDtoResponse> expectedList4 = new ArrayList<>(expectedList1);

        List<BookingDtoResponse> expectedList5 = new ArrayList<>();
        expectedList5.add(response2);
        expectedList5.add(response3);

        int from = 0, size = 10;

        MvcResult result1 = this.mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResponse> actualList1 = List.of(objectMapper.readValue(
                result1.getResponse().getContentAsString(),
                BookingDtoResponse[].class
        ));

        MvcResult result2 = this.mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "CURRENT")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResponse> actualList2 = List.of(objectMapper.readValue(
                result2.getResponse().getContentAsString(),
                BookingDtoResponse[].class
        ));

        MvcResult result3 = this.mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "PAST")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResponse> actualList3 = List.of(objectMapper.readValue(
                result3.getResponse().getContentAsString(),
                BookingDtoResponse[].class
        ));

        MvcResult result4 = this.mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "FUTURE")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResponse> actualList4 = List.of(objectMapper.readValue(
                result4.getResponse().getContentAsString(),
                BookingDtoResponse[].class
        ));

        MvcResult result5 = this.mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "REJECTED")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDtoResponse> actualList5 = List.of(objectMapper.readValue(
                result5.getResponse().getContentAsString(),
                BookingDtoResponse[].class
        ));

        assertEquals(expectedList1, actualList1);
        assertEquals(expectedList2, actualList2);
        assertEquals(expectedList3, actualList3);
        assertEquals(expectedList4, actualList4);
        assertEquals(expectedList5, actualList5);

        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "UNSUPPORTED")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId))
                )
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UnsupportedStateException));
    }

}