package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query("UPDATE User e SET " +
            "e.name = CASE WHEN :#{#user.name} IS NOT NULL THEN :#{#user.name} ELSE e.name END, " +
            "e.email = CASE WHEN :#{#user.email} IS NOT NULL THEN :#{#user.email} ELSE e.email END " +
            "WHERE e.id = :userId")
    void updateUserFields(User user, Long userId);

}
