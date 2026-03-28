package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void findAllUsers() {
        userController.createUser(createTestUser());
        userController.createUser(createTestUser());

        Collection<User> users = userController.findAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void createUser() {
        User user = createTestUser();
        User created = userController.createUser(user);

        assertNotNull(created.getId());
        assertEquals(user.getEmail(), created.getEmail());
        assertEquals(1, userController.findAllUsers().size());
    }

    @Test
    void updateUser() {
        User user = createTestUser();
        User created = userController.createUser(user);

        created.setEmail("new@new.com");
        User updated = userController.updateUser(created);

        assertEquals("new@new.com", updated.getEmail());
        assertEquals(created.getId(), updated.getId());
    }

    @Test
    void updateUserNullId () {
        User user = createTestUser();
        user.setId(null);

        assertThrows(ValidationException.class, () -> userController.updateUser(user));
    }

    @Test
    void updateUser999Id () {
        User user = createTestUser();
        user.setId(999L);

        assertThrows(ValidationException.class, () -> userController.updateUser(user));
    }

    private User createTestUser() {
        User user = new User();
        user.setEmail("user@user.com");
        user.setLogin("userLogin");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }
}
