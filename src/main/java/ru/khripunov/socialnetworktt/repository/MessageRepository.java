package ru.khripunov.socialnetworktt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.khripunov.socialnetworktt.model.Message;


@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
