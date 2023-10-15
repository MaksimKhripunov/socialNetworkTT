package ru.khripunov.socialnetworktt.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.khripunov.socialnetworktt.model.Friend;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Transactional
    @Query(value = "select friend_id from friends f where (select count(*) from friends fr where " +
            "(fr.person_id = :personId and fr.friend_id = f.friend_id) or (fr.person_id = f.friend_id and fr.friend_id = :personId)) = 2 " +
            "and f.person_id = :personId", nativeQuery = true)
    List<Long> findAllFriends(Long personId);

    @Transactional
    @Query(value = "select count(*) from friends f where f.person_id = :personId and f.friend_id = :friendId", nativeQuery = true)
    Integer findPair(Long personId, Long friendId);
}
