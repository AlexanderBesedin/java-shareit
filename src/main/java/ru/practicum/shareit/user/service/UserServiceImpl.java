package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto add(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.info("User {} has been CREATED", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        if (id == null || !userRepository.existsById(id)) {
            throw new NotFoundException("Cannot update a non-existent user");
        }

        for (User oldUser : userRepository.findAll()) {
            if (oldUser.getEmail().equals(userDto.getEmail()) && !oldUser.getId().equals(id)) {
                throw new DuplicateEmailException("Email cannot be duplicated");
            }
        }

        User user = userRepository.getReferenceById(id);

        if (userDto.getId() != null) user.setId(userDto.getId());
        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());

        userRepository.save(user);
        log.info("User {} has been UPDATED", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto get(Long id) {
        if (id == null || !userRepository.existsById(id)) {
            throw new NotFoundException("Cannot get a non-existent user");
        }
        log.info("Get a user with ID = {}", id);
        return UserMapper.toUserDto(userRepository.getReferenceById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        log.info("Get all users");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (id == null || !userRepository.existsById(id)) {
            throw new NotFoundException("Cannot delete a non-existent user");
        }
        userRepository.deleteById(id);
        log.info("Delete user with ID = {}", id);
    }
}
