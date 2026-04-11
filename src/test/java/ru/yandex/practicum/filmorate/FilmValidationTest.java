package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {

    private FilmController filmController;
    private Film film;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        film = new Film();
        film.setName("Test film");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
    }

    @Test
    void createValidFilmTest() {
        assertDoesNotThrow(() -> filmController.createFilm(film));
    }

    @Test
    void createNullNameFilmTest() {
        film.setName(null);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void createBlankNameFilmTest() {
        film.setName("");
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void createFilmDescriptionLongerThan200Test() {
        film.setDescription("a".repeat(201));
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void createFilmDescription200Test() {
        film.setDescription("a".repeat(200));
        assertDoesNotThrow(() -> filmController.createFilm(film));
    }

    @Test
    void createFilmNullReleaseTest() {
        film.setReleaseDate(null);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void createFilmIsBeforeCinemaBirthdayTest() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void createFilmNullDurationTest() {
        film.setDuration(null);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void createFilmNegativeDurationTest() {
        film.setDuration(-1);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void createFilmZeroDurationTest() {
        film.setDuration(0);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }
}
