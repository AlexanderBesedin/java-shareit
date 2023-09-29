package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId() != null ? item.getRequestId() : null
        );
    }

    public static Item toItem(User owner, ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemDto.getRequestId() != null ? itemDto.getRequestId() : null
        );
    }

    public static ItemWithBookingDto toItemDtoWithBooking(Item item, List<Booking> bookings, User user,
                                                          List<Comment> comments) {
        LocalDateTime time = LocalDateTime.now();

        Optional<Booking> lastBooking = bookings.stream()
                .filter(b -> user.getId().equals(b.getItem().getOwner().getId()))
                .filter(b -> b.getItem().getId().equals(item.getId()) && b.getStatus().equals(Status.APPROVED))
                .filter(b -> (b.getStart().isBefore(time) && b.getEnd().isAfter(time)) || b.getEnd().isBefore(time))
                .max(Comparator.comparing(Booking::getId));
        Optional<Booking> nextBooking = bookings.stream()
                .filter(b -> user.getId().equals(b.getItem().getOwner().getId()))
                .filter(b -> b.getItem().getId().equals(item.getId()) && b.getStatus().equals(Status.APPROVED))
                .filter(b -> b.getStart().isAfter(time))
                .min(Comparator.comparing(Booking::getStart));

        BookingForItemDto actualLastBooking = lastBooking
                .map(BookingMapper::toBookingForItemDto).orElse(null);
        BookingForItemDto actualNextBooking = nextBooking
                .map(BookingMapper::toBookingForItemDto).orElse(null);

        List<CommentDto> commentDtos = comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        return ItemWithBookingDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(item.getOwner().getId())
                .available(item.getAvailable())
                .lastBooking(actualLastBooking)
                .nextBooking(actualNextBooking)
                .comments(commentDtos)
                .build();
    }
}
