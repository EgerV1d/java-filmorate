package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.List;

@Repository
public class MpaRatingService {
    private final MpaRatingStorage mpaRatingStorage;

    public MpaRatingService(MpaRatingStorage mpaRatingStorage) {
        this.mpaRatingStorage = mpaRatingStorage;
    }

    public List<MpaRating> findAll() {
        return mpaRatingStorage.findAll();
    }

    public MpaRating findById(int id) {
        return mpaRatingStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id = " + id + " не найден"));
    }
}
