package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private final BookingService bookingService;
    private User booker;
    private BookingDto bookingDto;

    @BeforeEach
    public void setUp() throws Exception {
        booker = new User(1L, "user", "user@user.com");
        User owner = new User(2L, "user2", "user2@user.com");
        Item item = new Item(1L, "Дрель", "Ударная дрель", true, owner, null);

        bookingDto = new BookingDto(1L, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusDays(1),
                1L, UserMapper.toUserDto(booker), ItemMapper.toItemDto(item), Status.WAITING);
    }

    @Test
    @SneakyThrows
    void shouldCreate() {
        when(bookingService.add(any(), anyLong())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("user"));
    }

    @Test
    @SneakyThrows
    void shouldUpdate() {
        bookingDto.setStatus(Status.APPROVED);
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}?approved=true", 1)
                        .header("X-Sharer-User-Id", booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("user"));
    }

    @Test
    @SneakyThrows
    void shouldGetById() {
        when(bookingService.get(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("user"));
    }

    @Test
    @SneakyThrows
    void shouldGetAllBookingsForUser() {
        when(bookingService.getAllByUser(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto, bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("Дрель"))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].booker.name").value("user"));
    }

    @Test
    @SneakyThrows
    void shouldGetAllBookingsForOwner() {
        when(bookingService.getAllByOwner(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto, bookingDto));

        mvc.perform(get("/bookings/owner", 1)
                        .header("X-Sharer-User-Id", booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("Дрель"))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].booker.name").value("user"));
    }
}