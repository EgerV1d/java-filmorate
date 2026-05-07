package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;
    private UserDbStorage userDbStorage;

    @BeforeEach
    void setUp() {
        FilmRowMapper filmRowMapper = new FilmRowMapper();
        filmDbStorage = new FilmDbStorage(jdbcTemplate, filmRowMapper);
    }

    @Test
    void testFindAllFilms() {
        filmDbStorage.createFilm(createTestFilm());
        filmDbStorage.createFilm(createTestFilm("Test film 2"));

        Collection<Film> films = filmDbStorage.findAllFilms();

        assertThat(films).hasSize(2);
    }

    @Test
    void testFindFilmById() {
        Film film = filmDbStorage.createFilm(createTestFilm());
        Optional<Film> found = filmDbStorage.findFilmById(film.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(film.getId());
    }

    @Test
    void testFindFilmByIdNotFound() {
        Optional<Film> found = filmDbStorage.findFilmById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void testCreateFilm() {
        Film film = filmDbStorage.createFilm(createTestFilm());

        assertThat(film.getId()).isNotNull();
        assertThat(film.getName()).isEqualTo("Test film");
        assertThat(film.getDescription()).isEqualTo("Test Description");
        assertThat(film.getReleaseDate()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(film.getDuration()).isEqualTo(150);
        assertThat(film.getMpaRating().getId()).isEqualTo(1);
    }

    @Test
    void testUpdateFilm() {
        Film film = filmDbStorage.createFilm(createTestFilm());

        film.setName("Updated Name");
        film.setDescription("Updated Description");
        Film updated = filmDbStorage.updateFilm(film);

        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void testFilmExists() {
        Film film = filmDbStorage.createFilm(createTestFilm());

        assertThat(filmDbStorage.filmExists(film.getId())).isTrue();
        assertThat(filmDbStorage.filmExists(999L)).isFalse();
    }

    @Test
    void testGetPopularFilms() {
        UserRowMapper userRowMapper = new UserRowMapper();
        userDbStorage = new UserDbStorage(jdbcTemplate, userRowMapper);

        User user1 = userDbStorage.createUser(createTestUser("user1@test.com", "user1"));
        User user2 = userDbStorage.createUser(createTestUser("user2@test.com", "user2"));
        User user3 = userDbStorage.createUser(createTestUser("user3@test.com", "user3"));

        Film film1 = filmDbStorage.createFilm(createTestFilm("Film1"));
        Film film2 = filmDbStorage.createFilm(createTestFilm("Film2"));
        Film film3 = filmDbStorage.createFilm(createTestFilm("Film3"));

        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)",
                film1.getId(), user1.getId());
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)",
                film1.getId(), user2.getId());
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)",
                film2.getId(), user3.getId());

        List<Film> popular = filmDbStorage.getPopularFilms(2);

        assertThat(popular).hasSize(2);
        assertThat(popular.getFirst().getId()).isEqualTo(film1.getId());
    }

    @Test
    void testSaveFilmWithGenres() {
        Film film = createTestFilm("Film with genres");
        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(2, "Драма");
        film.setGenres(Set.of(genre1, genre2));

        Film saved = filmDbStorage.createFilm(film);

        assertThat(saved.getGenres()).hasSize(2);
        assertThat(saved.getGenres()).contains(genre1, genre2);
    }

    private Film createTestFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2026, 1, 1));
        film.setDuration(150);

        MpaRating mpaRating = new MpaRating(1, "G");
        film.setMpaRating(mpaRating);
        return film;
    }

    private Film createTestFilm() {
        return createTestFilm("Test film");
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
