package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAllFilms();
    }

    public Film findById(Long id) {
        return filmStorage.findFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    public Film create(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            throw new RuntimeException("Id должен быть указан");
        }
        if (!filmStorage.filmExists(film.getId())) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        return filmStorage.updateFilm(film);
    }

    public void addLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        if (!userStorage.userExists(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        film.getLikes().add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        if (!userStorage.userExists(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findAllFilms().stream()
                .sorted(Comparator.comparing(film -> -film.getLikes().size()))
                .limit(count)
                .toList();
    }
}
