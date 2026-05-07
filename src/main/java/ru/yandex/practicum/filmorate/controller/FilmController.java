package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAllFilms() {
        log.debug("Запрос на получение всех фильмов");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable Long id) {
        log.debug("Запрос на получение фильма по id: {}", id);
        return filmService.findById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.debug("Запрос на получение топ-{} фильмов", count);
        return filmService.getPopularFilms(count);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.debug("Запрос на создание фильма: {}", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.debug("Запрос на обновление фильма: {}", film);
        if (film.getId() == null) {
            log.warn("Попытка обновления фильма без ID");
            throw new ValidationException("Id фильма должен быть указан");
        }
        log.info("Обновлён фильм: {}", film);
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("Запрос на добавление лайка: filmId={}, userId={}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("Запрос на удаление лайка: filmId={}, userId={}", id, userId);
        filmService.removeLike(id, userId);
    }
}
