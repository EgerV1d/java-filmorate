package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
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
        return userStorage.createUser(user);
    }

    public User update(User user) {
        if (user.getId() == null) {
            throw new RuntimeException("Id должен быть указан");
        }
        if (!userStorage.userExists(user.getId())) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        return userStorage.updateUser(user);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public Collection<User> getFriends(Long userId) {
        User user = findById(userId);
        return user.getFriends().stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        Set<Long> commonFriendsId = user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .collect(Collectors.toSet());

        return commonFriendsId.stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }
}
