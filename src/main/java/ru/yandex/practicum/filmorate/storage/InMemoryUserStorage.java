package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private long nextId = 1;

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User createUser(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean userExists(Long id) {
        return users.containsKey(id);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        User user = users.get(userId);
        User other = users.get(otherId);

        if (user == null || other == null) {
            return List.of();
        }

        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            return List.of();
        }
        return user.getFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }
}
