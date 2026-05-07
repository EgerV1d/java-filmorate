package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAllUsers();

    Optional<User> findUserById(Long id);

    User createUser(User user);

    User updateUser(User user);

    boolean userExists(Long id);

    Collection<User> getCommonFriends(Long userId, Long otherId);

    Collection<User> getFriends(Long userId);
}
