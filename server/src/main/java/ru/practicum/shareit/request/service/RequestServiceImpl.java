package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto add(ItemRequestShortDto itemRequestShortDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException("Cannot create request with non-existent user")
                );
        ItemRequest request = requestRepository.save(RequestMapper.toItemRequest(itemRequestShortDto, user));
        log.info("Request id = {} with description = {} has been added ", request.getId(), request.getDescription());
        return RequestMapper.toItemRequestDto(request, itemRepository.findByRequestIdOrderByIdDesc(request.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto get(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException("Cannot get request with non-existent user")
                );
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("RequestId = %d not found.", requestId))
                );
        log.info("Get request id = {}", requestId);
        return RequestMapper.toItemRequestDto(request, itemRepository.findByRequestIdOrderByIdDesc(request.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException("Cannot get request with non-existent user")
                );
        List<ItemRequest> requests = requestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        log.info("Get all requests by user id = {}", userId);
        return requests
                .stream()
                .map(request -> RequestMapper.toItemRequestDto(request,
                        itemRepository.findByRequestIdOrderByIdDesc(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException("Cannot get request with non-existent user")
                );
        List<ItemRequest> requests = requestRepository.findByRequestorIdIsNotOrderByCreatedDesc(userId,
                PageRequest.of(from > 0 ? from / size : 0, size));
        log.info("Get all requests");
        return requests
                .stream()
                .map(request -> RequestMapper.toItemRequestDto(request,
                        itemRepository.findByRequestIdOrderByIdDesc(request.getId())))
                .collect(Collectors.toList());
    }
}
