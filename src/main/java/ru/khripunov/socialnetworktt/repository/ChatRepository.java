package ru.khripunov.socialnetworktt.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.khripunov.socialnetworktt.model.Chat;

import java.util.Optional;


@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Transactional
    @Query(value = "select * from chats c where (c.first_companion = :sender and c.second_companion = :recipient) or (c.first_companion = :recipient and c.second_companion = :sender)", nativeQuery = true)
    Optional<Chat> findByCompanions(Long sender, Long recipient);
}
