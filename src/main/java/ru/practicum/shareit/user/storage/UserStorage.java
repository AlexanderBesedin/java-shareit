package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User add(User user);

    User update(Long id, User user);

    User get(Long id);

    List<User> getAll();

    boolean checkExist(Long id);

    void delete(Long id);
}