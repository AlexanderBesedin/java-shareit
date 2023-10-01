package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
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
@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private final ItemService itemService;
    private ItemDto itemDto;
    private ItemWithBookingDto itemWithBookingDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1L, "Дрель", "Ударная дрель", true, null);
        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = start.plusDays(1);
        BookingForItemDto lastBooking = BookingForItemDto
                .builder()
                .id(1L)
                .bookerId(1L)
                .bookingStatus(Status.WAITING)
                .start(start)
                .end(end)
                .build();
        BookingForItemDto nextBooking = BookingForItemDto
                .builder()
                .id(1L)
                .bookerId(1L)
                .bookingStatus(Status.WAITING)
                .start(start.plusDays(2))
                .end(end.plusDays(2))
                .build();
        itemWithBookingDto = ItemWithBookingDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Ударная дрель")
                .ownerId(1L)
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }

    @Test
    @SneakyThrows
    void shouldCreate() {
        when(itemService.add(anyLong(), any())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Ударная дрель"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @SneakyThrows
    void shouldUpdate() {
        itemDto.setAvailable(false);
        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Ударная дрель"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    @SneakyThrows
    void shouldGetById() {
        when(itemService.get(anyLong(), anyLong())).thenReturn(itemWithBookingDto);

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Ударная дрель"))
                .andExpect(jsonPath("ownerId").value(1))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.lastBooking").isNotEmpty())
                .andExpect(jsonPath("$.nextBooking").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void shouldGetItemsByOwner() {
        when(itemService.getAllByUser(anyLong())).thenReturn(List.of(itemWithBookingDto, itemWithBookingDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Ударная дрель"))
                .andExpect(jsonPath("[0]ownerId").value(1))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].lastBooking").isNotEmpty())
                .andExpect(jsonPath("$[0].nextBooking").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void shouldGetItemsBySearch() {
        when(itemService.getAllByText(anyString(), anyLong())).thenReturn(List.of(itemDto, itemDto));

        mvc.perform(get("/items/search?text=уДАрнаЯ")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Ударная дрель"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    @SneakyThrows
    void shouldAddCommentTest() {
        Item item = new Item(1L, "Дрель", "Ударная дрель", true,
                new User(1L, "user", "user@user.com"), null);

        Comment comment = Comment
                .builder()
                .id(1L)
                .authorName("author")
                .item(item)
                .text("some text")
                .created(LocalDateTime.now())
                .build();

        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(comment);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(objectMapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("some text"))
                .andExpect(jsonPath("$.item").isNotEmpty())
                .andExpect(jsonPath("$.authorName").value("author"));
    }
}