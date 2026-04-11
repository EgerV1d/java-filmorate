package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        log.debug("Запрос на получение всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable Long id) {
        log.debug("Запрос на получение пользователя по id: {}", id);
        return userService.findById(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        log.debug("Запрос на получение списка друзей: id={}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.debug("Запрос на получение общих друзей: id={}, otherId={}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.debug("Запрос на создание пользователя: {}", user);
        validate(user);
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя заменено на логин: {}", user.getLogin());
        }
        log.info("Создан пользователь: {}", user);
        return userService.create(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.debug("Запрос на обновление пользователя: {}", user);
        if (user.getId() == null) {
            log.warn("Попытка обновления пользователя без ID");
            throw new ValidationException("Id пользователя должен быть указан");
        }
        validate(user);

        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя заменено на логин: {}", user.getLogin());
        }
        log.info("Обновлён пользователь: {}", user);
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.debug("Запрос на добавление в друзья: id={}, friendId={}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.debug("Запрос на удаление из друзей: id={}, friendId={}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    private void validate(User user) {
        if (!StringUtils.hasText(user.getEmail())) {
            log.warn("Ошибка валидации: пустой email");
            throw new ValidationException("Электронная почта не может быть пустой");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: email не содержит символ @: {}", user.getEmail());
            throw new ValidationException("Электронная почта должна содержать символ @");
        }

        if (!StringUtils.hasText(user.getLogin())) {
            log.warn("Ошибка валидации: пустой логин");
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации: логин содержит пробелы: {}", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }

        if (user.getBirthday() == null) {
            log.warn("Ошибка валидации: дата рождения не указана");
            throw new ValidationException("Дата рождения должна быть указана");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации: дата рождения {} в будущем", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
