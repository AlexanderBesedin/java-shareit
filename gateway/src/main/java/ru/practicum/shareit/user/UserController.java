package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateGroup;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Validated(CreateGroup.class) UserDto userDto) {
        return userClient.add(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Valid @PathVariable("userId") Long id, @RequestBody UserDto userDto) {
        return userClient.update(userDto, id);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable("userId") Long id) {
        return userClient.get(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long id) {
        userClient.delete(id);
    }
}
