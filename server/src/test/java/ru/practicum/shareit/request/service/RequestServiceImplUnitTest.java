package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplUnitTest {
    @InjectMocks
    private RequestServiceImpl requestService;
    @Mock
    private UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    private RequestRepository requestRepository;
    private User user1;
    private User user2;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        user1 = new User(1L, "user", "user@user.com");
        user2 = new User(2L, "user2", "user2@user.com");
        request = new ItemRequest(1L, "нужна дрель", user2, LocalDateTime.now());
    }

    @Test
    void shouldAddRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));
        when(requestRepository.save(any())).thenReturn(request);
        when(itemRepository.findByRequestIdOrderByIdDesc(anyLong())).thenReturn(List.of());

        assertEquals(RequestMapper.toItemRequestDto(request, List.of()),
                requestService.add(new ItemRequestShortDto(request.getDescription()), user2.getId()));
    }

    @Test
    void shouldThrowIfAddRequestNotExistUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> requestService.add(new ItemRequestShortDto(request.getDescription()), 3L));
    }

    @Test
    void shouldGetByRequestIdAndUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));

        assertDoesNotThrow(() -> requestService.get(user1.getId(), request.getId()));
    }

    @Test
    void shouldThrowIfRequestWithUserWrongId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.get(1L, 1L));
    }

    @Test
    void shouldThrowIfRequestWithWrongId() {
        when(userRepository.findById(anyLong())).thenReturn(
                Optional.of(new User(1L, "user", "user@user.com")));
        assertThrows(NotFoundException.class, () -> requestService.get(1L, 1L));
    }

    @Test
    void shouldGetAllRequestsByOwner() {
        ItemRequest request2 = new ItemRequest(2L, "нужна ножовка", user2, LocalDateTime.now());
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));
        when(requestRepository.findByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(request, request2));

        assertEquals(2, requestService.getByUserId(user2.getId()).size());
    }

    @Test
    void shouldThrowIfAskAllRequestsOfOwnerByWrongUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.getByUserId(1L));
    }

    @Test
    void shouldGetAllRequestsOfOtherUsers() {
        ItemRequest request2 = new ItemRequest(2L, "нужна ножовка", user1, LocalDateTime.now());
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));
        when(requestRepository.findByRequestorIdIsNotOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(List.of(request2));

        assertEquals(1, requestService.getAll(user2.getId(), 0, 1).size());
    }

    @Test
    void shouldThrowIfAskAllRequestsByWrongUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.getAll(1L, 0, 1));
    }
}