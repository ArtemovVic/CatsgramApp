package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();


    public Collection<User> getUsers() {
        return users.values();
    }


    public User addUser(User user) {
        // проверяем выполнение необходимых условий
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (users.values().stream().anyMatch(existUser -> existUser.getEmail().equals(user.getEmail()))){
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        // формируем дополнительные данные
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        // сохраняем нового пользователя в памяти приложения
        users.put(user.getId(), user);
        return user;
    }


    public User update(User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (users.values().stream().anyMatch(existUser -> existUser.getEmail().equals(newUser.getEmail()))) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            // если пользователь найден и все условия соблюдены, обновляем её содержимое
            if (newUser.getEmail() != null){
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getUsername() != null){
                oldUser.setUsername(newUser.getUsername());
            }
            if (newUser.getPassword() != null){
                oldUser.setPassword(newUser.getPassword());
            }
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    public User findUserByEmail(String email) {
        return users.values().stream()
                .filter(p -> p.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(String.format("Пост № %s не найден", email)));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

}
