package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface MessageDao extends JpaRepository<Message, Integer> {
    @Query(value = "select * from message where sender = ?1 and receiver = ?2", nativeQuery = true)
    List<Message> receive(Integer sender, Integer receiver);
}
