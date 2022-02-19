package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Modifying
    @Query(value = "UPDATE user SET avatar = ?1 WHERE username= ?2", nativeQuery = true)
    void updateAvatarByUsername (byte[] avatar, String username);

    @Modifying
    @Query(value = "UPDATE user SET enabled = true WHERE username= ?1", nativeQuery = true)
    void enableUser (String username);
}

