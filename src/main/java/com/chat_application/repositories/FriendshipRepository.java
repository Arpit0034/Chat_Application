package com.chat_application.repositories;

import com.chat_application.entity.Friendship;
import com.chat_application.entity.User;
import com.chat_application.entity.enums.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship,Long> {

    Optional<Friendship> findByUser1AndUser2(User user, User friend);

    Optional<Friendship> findByUser2AndUser1(User friend, User user);

    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user OR f.user2 = :user) AND f.status = :status")
    List<Friendship> findByUser1OrUser2AndStatus(@Param("user") User user, @Param("status") FriendStatus status);

    List<Friendship> findByUser2AndStatus(User user, FriendStatus friendStatus);

    @Modifying
    @Query("DELETE FROM Friendship f WHERE f.user1 = :user OR f.user2 = :user")
    void deleteByUser1OrUser2(@Param("user") User user);

    List<Friendship> findByUser1AndStatus(User user, FriendStatus friendStatus);

    @Query("SELECT f FROM Friendship f WHERE (f.user1.id = :id1 AND f.user2.id = :id2) OR (f.user1.id = :id2 AND f.user2.id = :id1)")
    Optional<Friendship> findBidirectionalByUserIds(@Param("id1") Long id1, @Param("id2") Long id2);

}
