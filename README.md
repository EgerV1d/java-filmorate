# Filmorate

## Схема базы данных

![Filmorate Database.png](docs/Filmorate%20Database.png)

## Описание схемы

База данных состоит из следующих основных таблиц:

- **users** - пользователи приложения
- **films** - фильмы
- **genres** - справочник жанров
- **mpa_rating** - справочник рейтингов MPA
- **film_genre** - связь фильмов и жанров
- **likes** - лайки пользователей на фильмы
- **friendships** - дружба между пользователями со статусом
- **friendship_status** - справочник статусов дружбы

## Примеры запросов

### 1. Получение всех фильмов
````
SELECT * 
FROM films;
````

### 2. Получение топ-10 популярных фильмов (по количеству лайков)
````
SELECT f.film_id, 
       f.name, 
       COUNT(l.user_id) AS likes_count
FROM films f
LEFT JOIN likes l ON f.film_id = l.film_id
GROUP BY f.film_id, f.name
ORDER BY likes_count DESC
LIMIT 10;
````

### 3. Получение всех пользователей, поставивших лайк фильму
````
SELECT u.user_id, 
       u.login, 
       u.name
FROM likes l
JOIN users u ON l.user_id = u.user_id
WHERE l.film_id = 1;
````
