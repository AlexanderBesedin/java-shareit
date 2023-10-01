package ru.practicum.shareit.user;

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
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    private final ObjectMapper objectMapper;
    @MockBean
    private final UserService userService;
    private final MockMvc mockMvc;
    private UserDto request;

    @BeforeEach
    void setUp() {
        request = new UserDto(null, "user", "user@user.com");
    }

    @Test
    @SneakyThrows
    void shouldCreate() {
        when(userService.add(any())).thenReturn(new UserDto(1L, request.getName(), request.getEmail()));

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@user.com"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    @SneakyThrows
    void shouldUpdate() {
        when(userService.update(anyLong(), any()))
                .thenReturn(new UserDto(1L, "update", "update@user.com"));

        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("update@user.com"))
                .andExpect(jsonPath("$.name").value("update"));
    }

    @Test
    @SneakyThrows
    void shouldGetById() {
        when(userService.get(any())).thenReturn(new UserDto(1L, request.getName(), request.getEmail()));

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@user.com"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    @SneakyThrows
    void shouldGetAll() {
        UserDto user1 = new UserDto(1L, request.getName(), request.getEmail());
        UserDto user2 = new UserDto(2L, "anyUser", "anyUser@user.com");

        when(userService.getAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("user@user.com"))
                .andExpect(jsonPath("$[0].name").value("user"));
    }

    @Test
    @SneakyThrows
    void shouldDeleteById() {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void handlerNotFoundExceptionTest() {
        when(userService.add(any())).thenThrow(new NotFoundException("error text"));

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void handlerDuplicateEmailExceptionTest() {
        when(userService.add(any())).thenThrow(new DuplicateEmailException("Email cannot be duplicated"));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DuplicateEmailException))
                .andExpect(result -> assertEquals("Email cannot be duplicated",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }
}