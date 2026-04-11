package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void findAllFilms() {
        filmController.createFilm(createTestFilm());
        filmController.createFilm(createTestFilm());

        Collection<Film> films = filmController.findAllFilms();
        assertEquals(2, films.size());
    }

    @Test
    void createFilm() {
        Film film = createTestFilm();
        Film created = filmController.createFilm(film);

        assertNotNull(created.getId());
        assertEquals(film.getName(), created.getName());
        assertEquals(1, filmController.findAllFilms().size());
    }

    @Test
    void updateFilm() {
        Film film = createTestFilm();
        Film created = filmController.createFilm(film);

        created.setName("Updated Name");
        Film updated = filmController.updateFilm(created);

        assertEquals("Updated Name", updated.getName());
        assertEquals(created.getId(), updated.getId());
    }

    @Test
    void updateFilmNullId() {
        Film film = createTestFilm();
        film.setId(null);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
    }

    @Test
    void updateFilm999Id() {
        Film film = createTestFilm();
        film.setId(999L);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
    }

    private Film createTestFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        return film;
    }
}
