package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAllFilms() {
        log.debug("Запрос на получение всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.debug("Запрос на создание фильма: {}", film);
        validateFilm(film);
        film.setId(nextId++);
        films.put(film.getId(), film);
        log.info("Создан фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.debug("Запрос на обновление фильма: {}", film);
        if (film.getId() == null) {
            log.warn("Попытка обновления фильма без ID");
            throw new ValidationException("Id фильма должен быть указан");
        }

        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с id {} не найден", film.getId());
            throw new ValidationException("Фильм с id = \" + film.getId() + \" не найден");
        }
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Обновлён фильм: {}", film);
        return film;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка валидации: пустое название фильма");
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Ошибка валидации: описание фильма превышает 200 символов");
            throw new ValidationException("Описание фильма не может быть длиннее 200 символов");
        }

        if (film.getReleaseDate() == null) {
            log.warn("Ошибка валидации: дата релиза не указана");
            throw new ValidationException("Дата релиза должна быть указана");
        }

        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.warn("Ошибка валидации: дата релиза {} раньше 28.12.1895", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() == null || (film.getDuration() <= 0)) {
            log.warn("Ошибка валидации: продолжительность фильма = {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
