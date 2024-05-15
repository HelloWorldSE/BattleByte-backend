package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface MessageDao extends JpaRepository<Message, Integer> {

    Page<Message> findMessagesBySender(Integer sender, Pageable pageable);

    @Query(value = "select * from message where receiver = ?1 or receiver = -1", nativeQuery = true)
    Page<Message> findMessagesByReceiver(Integer receiver, Pageable pageable);
}
