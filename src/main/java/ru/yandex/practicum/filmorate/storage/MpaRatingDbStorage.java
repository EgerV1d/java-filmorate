package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRatingDbStorage implements MpaRatingStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<MpaRating> mapper = (rs, rowNum) ->
            new MpaRating(rs.getInt("mpa_rating_id"), rs.getString("name"));

    public MpaRatingDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<MpaRating> findAll() {
        String sql = "SELECT * FROM mpa_rating ORDER BY mpa_rating_id";
        return jdbc.query(sql, mapper);
    }

    @Override
    public Optional<MpaRating> findById(int id) {
        String sql = "SELECT * FROM mpa_rating WHERE mpa_rating_id = ?";
        return jdbc.query(sql, mapper, id).stream().findFirst();
    }

    @Override
    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM mpa_rating WHERE mpa_rating_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count > 0;
    }
}
