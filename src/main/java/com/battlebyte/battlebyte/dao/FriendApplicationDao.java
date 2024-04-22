package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.FriendApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendApplicationDao extends JpaRepository<FriendApplication, Integer> {

}
