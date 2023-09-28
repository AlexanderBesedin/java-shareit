package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    @Transactional
    public ItemDto add(Long owner, ItemDto itemDto) {
        User user = userRepository.findById(owner)
                .orElseThrow(
                        () -> new NotFoundException("Cannot create item with non-existent user")
                );
        Item item = itemRepository.save(ItemMapper.toItem(user, itemDto));
        log.info("Item added: id = {}, name = {}, ownerId = {}", item.getId(), item.getName(), owner);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(Long id, Long owner, ItemDto itemDto) {
        if (id == null || !itemRepository.existsById(id)) {
            throw new NotFoundException("Cannot update non-existent item");
        }

        if (owner == null || !userRepository.existsById(owner)) {
            throw new NotFoundException("Cannot update item with non-existent user");
        }
      
        Item findItem  = itemRepository.findByOwnerIdAndId(owner, id)
                .orElseThrow(
                        () -> new NotOwnerException("Cannot get items with non-added user"));

        itemRepository.findAllById(Collections.singleton(id)).remove(findItem);
        //Проверяем поля полученного itemDto на null
        if (itemDto.getId() != null) findItem.setId(itemDto.getId());
        if (itemDto.getName() != null) findItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) findItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) findItem.setAvailable(itemDto.getAvailable());
        if (itemDto.getRequest() != null) findItem.setRequest(itemDto.getRequest());

        log.info("Item {} has been UPDATED", findItem);
        return ItemMapper.toItemDto(itemRepository.save(findItem));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemWithBookingDto get(Long id, Long userId) {
        if (id == null || !itemRepository.existsById(id)) {
            throw new NotFoundException("Cannot get non-existent item");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException("Cannot get items with non-existent user")
                );
        log.info("Get item id = {}", id);
        return ItemMapper.toItemDtoWithBooking(itemRepository.getReferenceById(id),
                bookingRepository.findByItemOwnerId(userId), user, commentRepository.findByItemId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemWithBookingDto> getAllByUser(Long owner) {
        User user = userRepository.findById(owner)
                .orElseThrow(
                        () -> new NotFoundException("Cannot get items with non-existent user")
                );
        log.info("Get all items by owner id = {}", owner);
        return itemRepository.findAllByOwnerId(owner)
                .stream()
                .map(item -> ItemMapper.toItemDtoWithBooking(item,
                        bookingRepository.findByItem(item), user,
                        commentRepository.findByItemId(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllByText(String text, Long renter) {
        if (renter == null || !userRepository.existsById(renter)) {
            throw new NotFoundException("Cannot get items by non-existent user");
        }
        if (text == null || text.isEmpty()) return List.of();

        return itemRepository.searchByText(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Comment addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException("Non-existent user cannot add a comment")
                );
        Item item = itemRepository.findById(itemId)
                .orElseThrow(
                        () -> new NotFoundException("Impossible to add a comment for a non-existent item")
                );

        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId,
                itemId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BookingException("The user did not book the item or the booking period has not expired");
        } else {
            return commentRepository.save(CommentMapper.toComment(commentDto, user, item));
        }
    }
}
