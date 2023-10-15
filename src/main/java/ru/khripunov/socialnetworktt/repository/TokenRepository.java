package ru.khripunov.socialnetworktt.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.khripunov.socialnetworktt.model.Token;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Transactional
    @Query(value = "select * from invalid_tokens it where it.token = :jwt", nativeQuery = true)
    Optional<Token> findByToken(String jwt);
}
