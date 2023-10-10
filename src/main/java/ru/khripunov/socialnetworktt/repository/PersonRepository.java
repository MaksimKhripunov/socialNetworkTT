package ru.khripunov.socialnetworktt.repository;


import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import ru.khripunov.socialnetworktt.model.Person;


import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query(value = "select * from users u where u.username = :username", nativeQuery = true)
    Optional<Person> findByUsername(String username);

    @Transactional
    @Query(value = "update users u set delete_time=now()+'31d' where u.username = :username", nativeQuery = true)
    @Modifying
    void deleteByUsername(String username);

    @Transactional
    @Query(value = "delete from users u where u.delete_time<now()", nativeQuery = true)
    @Modifying
    void deleteUsers();


}
