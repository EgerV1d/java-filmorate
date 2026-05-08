package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userDbStorage;

    @BeforeEach
    void setUp() {
        UserRowMapper userRowMapper = new UserRowMapper();
        userDbStorage = new UserDbStorage(jdbcTemplate, userRowMapper);
    }

    @Test
    void testFindAllUsers() {
        userDbStorage.createUser(createTestUser());
        userDbStorage.createUser(createTestUser("test2@example.com", "user2"));

        Collection<User> users = userDbStorage.findAllUsers();
        assertThat(users).hasSize(2);
    }

    @Test
    void testFindUserById() {
        User user = userDbStorage.createUser(createTestUser());
        Optional<User> found = userDbStorage.findUserById(user.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(user.getId());
    }

    @Test
    void testFindUserByIdNotFound() {
        Optional<User> found = userDbStorage.findUserById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void testCreateUser() {
        User user = userDbStorage.createUser(createTestUser());

        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isEqualTo("test1@example.com");
        assertThat(user.getLogin()).isEqualTo("user1");
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getBirthday()).isEqualTo("1990-01-01");
    }

    @Test
    void testUpdateUser() {
        User user = userDbStorage.createUser(createTestUser());

        user.setName("Updated Name");
        user.setEmail("updated@example.com");
        User updated = userDbStorage.updateUser(user);

        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void testUserExists() {
        User user = userDbStorage.createUser(createTestUser());
        assertThat(userDbStorage.userExists(user.getId())).isTrue();
        assertThat(userDbStorage.userExists(999L)).isFalse();
    }

    @Test
    void testGetFriends() {
        User user1 = userDbStorage.createUser(createTestUser("user1@test.com", "user1"));
        User user2 = userDbStorage.createUser(createTestUser("user2@test.com", "user2"));

        String sql = "INSERT INTO friendships (user_id, friend_id, status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, user1.getId(), user2.getId(), 2);
        Collection<User> friends = userDbStorage.getFriends(user1.getId());

        assertThat(friends).hasSize(1);
        assertThat(friends.iterator().next().getId()).isEqualTo(user2.getId());
    }

    @Test
    void testGetFriendsWithoutFriends() {
        User user = userDbStorage.createUser(createTestUser());
        Collection<User> friends = userDbStorage.getFriends(user.getId());

        assertThat(friends).isEmpty();
    }

    @Test
    void testGetCommonFriends() {
        User user1 = userDbStorage.createUser(createTestUser("user1@test.com", "user1"));
        User user2 = userDbStorage.createUser(createTestUser("user2@test.com", "user2"));
        User friend = userDbStorage.createUser(createTestUser("friend@test.com", "friend"));

        jdbcTemplate.update("INSERT INTO friendships (user_id, friend_id, status_id) VALUES (?, ?, ?)",
                user1.getId(), friend.getId(), 2);
        jdbcTemplate.update("INSERT INTO friendships (user_id, friend_id, status_id) VALUES (?, ?, ?)",
                user2.getId(), friend.getId(), 2);

        Collection<User> commonFriend = userDbStorage.getCommonFriends(user1.getId(), user2.getId());

        assertThat(commonFriend).hasSize(1);
        assertThat(commonFriend.iterator().next().getId()).isEqualTo(friend.getId());
    }

    @Test
    void testGetCommonFriendsNoCommon() {
        User user1 = userDbStorage.createUser(createTestUser("user1@test.com", "user1"));
        User user2 = userDbStorage.createUser(createTestUser("user2@test.com", "user2"));
        User friend = userDbStorage.createUser(createTestUser("friend@test.com", "friend"));

        jdbcTemplate.update("INSERT INTO friendships (user_id, friend_id, status_id) VALUES (?, ?, ?)",
                user1.getId(), friend.getId(), 2);

        Collection<User> commonFriend = userDbStorage.getCommonFriends(user1.getId(), user2.getId());

        assertThat(commonFriend).isEmpty();
    }

    private User createTestUser() {
        return createTestUser("test1@example.com", "user1");
    }

    private User createTestUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }
}
