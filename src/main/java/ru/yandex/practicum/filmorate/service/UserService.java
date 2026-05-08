package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAllUsers();
    }

    public User findById(Long id) {
        return userStorage.findUserById(id).orElseThrow(() -> new NotFoundException(
                "Пользователь с id = " + id + " не найден"));
    }

    public User create(User user) {
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя заменено на логин: {}", user.getLogin());
        }
        validate(user);
        User created = userStorage.createUser(user);
        log.info("Создан пользователь: id={}, email={}", created.getId(), created.getEmail());
        return created;
    }

    public User update(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (!userStorage.userExists(user.getId())) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
        validate(user);
        User updated = userStorage.updateUser(user);
        log.info("Обновлён пользователь: id={}, email={}", updated.getId(), updated.getEmail());
        return updated;
    }

    public void addFriend(Long userId, Long friendId) {
        if (!userStorage.userExists(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!userStorage.userExists(friendId)) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (!userStorage.userExists(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!userStorage.userExists(friendId)) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        userStorage.removeFriend(userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        User user = findById(userId);
        return userStorage.getFriends(userId);
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        return userStorage.getCommonFriends(userId, friendId);
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
