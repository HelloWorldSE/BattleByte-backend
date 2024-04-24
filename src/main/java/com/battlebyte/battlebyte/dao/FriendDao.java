package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.Friend;
import com.battlebyte.battlebyte.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendDao extends JpaRepository<Friend, Integer> {
    @Query(value = "SELECT * FROM friend WHERE small_id = ?1 AND large_id = ?2", nativeQuery = true)
    public Friend findFriend(Integer smallId, Integer largeId);
}
