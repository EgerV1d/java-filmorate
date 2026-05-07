package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {

    private UserController userController;
    private User user;

    @BeforeEach
    void setUp() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);
        user = new User();
        user.setEmail("user@user.com");
        user.setLogin("userLogin");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void createValidUserTest() {
        assertDoesNotThrow(() -> userController.createUser(user));
    }

    @Test
    void createUserNullNameTest() {
        user.setName(null);
        User nullName = userController.createUser(user);
        assertEquals(user.getLogin(), nullName.getName());
    }

    @Test
    void createUserBlankNameTest() {
        user.setName("");
        User blankName = userController.createUser(user);
        assertEquals(user.getLogin(), blankName.getName());
    }

    @Test
    void createUserNullEmailTest() {
        user.setEmail(null);
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUserBlankEmailTest() {
        user.setEmail("");
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUserEmailContainsAtTest() {
        user.setEmail("useruser.com");
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUserNullLoginTest() {
        user.setLogin(null);
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUserBlankLoginTest() {
        user.setLogin("");
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUserLoginContainsSpaceTest() {
        user.setLogin("user Login");
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUserNullBirthdayTest() {
        user.setBirthday(null);
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUserBirthdayInFutureTest() {
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }
}
