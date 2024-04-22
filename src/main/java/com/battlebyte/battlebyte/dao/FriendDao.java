package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.Friend;
import com.battlebyte.battlebyte.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendDao extends JpaRepository<Friend, Integer> {

}
