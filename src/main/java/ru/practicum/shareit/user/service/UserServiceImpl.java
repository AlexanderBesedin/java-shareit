package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public UserDto add(UserDto userDto) {
        if (validateEmail(userDto)) {
            throw new DuplicateEmailException("Email cannot be duplicated");
        }
        User user = userStorage.add(UserMapper.toUser(userDto));
        log.info("User {} has been CREATED", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        if (!userStorage.checkExist(id) || id == null) {
            throw new NotFoundException("Cannot update a non-existent user");
        }

        User user = userStorage.find(id);
        User newUser = UserMapper.toUser(userDto);

        for (User oldUser : userStorage.findAll()) {
            String email = oldUser.getEmail();
            Long userId = oldUser.getId();
            if (email.equals(newUser.getEmail()) && !userId.equals(id)) {
                throw new DuplicateEmailException("Email cannot be duplicated");
            }
        }

        if (newUser.getId() == null) newUser.setId(id);
        if (newUser.getName() == null) newUser.setName(user.getName());
        if (newUser.getEmail() == null) newUser.setEmail(user.getEmail());

        userStorage.update(id, newUser);
        log.info("User {} has been UPDATED", newUser);
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto get(Long id) {
        if (!userStorage.checkExist(id) || id == null) {
            throw new NotFoundException("Cannot get a non-existent user");
        }
        User user = userStorage.find(id);
        log.info("Get a user with ID = {}", id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        log.info("Get all users");
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!userStorage.checkExist(id) || id == null) {
            throw new NotFoundException("Cannot delete a non-existent user");
        }
        itemStorage.deleteUserWithItems(id);
        userStorage.delete(id);
        log.info("Delete user with ID = {}", id);
    }

    private boolean validateEmail(UserDto userDto) {
         return userStorage.findAll()
                .stream()
                .anyMatch(user -> user.getEmail().equals(userDto.getEmail()));
    }
}
