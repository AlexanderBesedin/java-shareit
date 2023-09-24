package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateGroup;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto add(@RequestBody @Validated(CreateGroup.class)UserDto userDto) {
        return userService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Valid @PathVariable("userId") Long id, @RequestBody UserDto userDto) {
        return userService.update(id, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable("userId") Long id) {
        return userService.get(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long id) {
        userService.delete(id);
    }
}
