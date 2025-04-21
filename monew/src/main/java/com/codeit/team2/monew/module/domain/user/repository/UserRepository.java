package com.codeit.team2.monew.module.domain.user.repository;

import com.codeit.team2.monew.module.domain.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmailAndDeletedFalse(String email);

    boolean existsByNicknameAndDeletedFalse(String nickname);

    Optional<User> findByEmailAndPasswordAndDeletedFalse(String email, String password);

}
