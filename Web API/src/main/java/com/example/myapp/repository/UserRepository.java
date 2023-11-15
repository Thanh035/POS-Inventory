package com.example.myapp.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.myapp.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    String USERS_BY_LOGIN_CACHE = "usersByLogin";

//    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

    Page<User> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.fullname LIKE %:filter% OR u.email LIKE %:filter% OR u.phoneNumber LIKE %:filter%")
    Page<User> findAllWithFilter(String filter, Pageable pageable);

    Optional<User> findOneByEmailIgnoreCase(String email);

    Optional<User> findOneByLogin(String login);

    @EntityGraph(attributePaths = "roles")
//    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<User> findOneWithRolesByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "roles")
//    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<User> findOneWithRolesByLogin(String login);

    Optional<User> findOneByResetKey(String resetKey);
}
