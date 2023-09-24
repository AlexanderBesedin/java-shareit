package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment, User author) {
        return CommentDto.builder()
                .id(comment.getId())
                .author(author.getName())
                .created(comment.getCreated())
                .text(comment.getText())
                .build();
    }

    public static Comment toComment(CommentDto commentDto, User author, Item item) {
        return Comment.builder()
                .authorName(author.getName())
                .created(LocalDateTime.now())
                .text(commentDto.getText())
                .item(item)
                .build();
    }
}
