package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.Date;
import java.util.*;

@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper filmRowMapper) {
        this.jdbc = jdbc;
        this.filmRowMapper = filmRowMapper;
    }

    @Override
    public Collection<Film> findAllFilms() {
        String sql = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa_rating m ON f.mpa_rating_id = m.mpa_rating_id";
        List<Film> films = jdbc.query(sql, filmRowMapper);
        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
        }
        return films;
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        String sql = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa_rating m ON f.mpa_rating_id = m.mpa_rating_id " +
                "WHERE f.film_id = ?";
        Optional<Film> film = jdbc.query(sql, filmRowMapper, id).stream().findFirst();
        film.ifPresent(f -> f.setGenres(getGenresByFilmId(f.getId())));
        return film;
    }

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null);
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpaRating() != null && film.getMpaRating().getId() != null
                    ? film.getMpaRating().getId()
                    : null);
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        updateGenres(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? " +
                "WHERE film_id = ?";
        jdbc.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null,
                film.getDuration(),
                film.getMpaRating() != null ? film.getMpaRating().getId() : null,
                film.getId()
        );
        updateGenres(film);
        return film;
    }

    @Override
    public boolean filmExists(Long id) {
        String sql = "SELECT COUNT(*) FROM films WHERE film_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count > 0;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, m.name AS mpa_name, COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN mpa_rating m ON f.mpa_rating_id = m.mpa_rating_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, m.name " +
                "ORDER BY likes_count DESC, f.film_id DESC LIMIT ?";
        List<Film> films = jdbc.query(sql, filmRowMapper, count);
        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
        }
        return films;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbc.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbc.update(sql, filmId, userId);
    }

    private Set<Genre> getGenresByFilmId(Long filmId) {
        String sql = "SELECT g.genre_id, g.name FROM film_genre fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id WHERE fg.film_id = ?";
        return new HashSet<>(jdbc.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("name")
                ), filmId));
    }

    private void updateGenres(Film film) {
        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbc.update(deleteSql, film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            List<Object[]> args = film.getGenres().stream()
                    .map(genre -> new Object[]{film.getId(), genre.getId()})
                    .toList();
            jdbc.batchUpdate(sql, args);
        }
    }
}
