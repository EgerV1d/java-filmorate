package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private long nextId = 1;

    @GetMapping
    public Collection<User> findAllUsers() {
        log.debug("Запрос на получение всех пользователей");
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.debug("Запрос на создание пользователя: {}", user);
        validate(user);
        user.setId(nextId++);

        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя заменено на логин: {}", user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.debug("Запрос на обновление пользователя: {}", user);
        if (user.getId() == null) {
            log.warn("Попытка обновления пользователя без ID");
            throw new ValidationException("Id пользователя должен быть указан");
        }

        if (!userExists(user.getId())) {
            log.warn("Пользователь с id {} не найден", user.getId());
            throw new ValidationException("Пользователь с id = " + user.getId() + " не найден");
        }
        validate(user);

        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя заменено на логин: {}", user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Обновлён пользователь: {}", user);
        return user;
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

    boolean userExists(Long id) {
        return users.containsKey(id);
    }
}
