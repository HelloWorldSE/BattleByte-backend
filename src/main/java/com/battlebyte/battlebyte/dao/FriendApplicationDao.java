package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.FriendApplication;
import com.battlebyte.battlebyte.entity.dto.FriendDTO;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendApplicationDao extends JpaRepository<FriendApplication, Integer> {
    @Query(value = "SELECT \n" +
            "    u.id,\n" +
            "    u.user_name AS userName,\n" +
            "    u.avatar,\n" +
            "    u.user_email AS userEmail,\n" +
            "    f.id AS friendId\n" +
            "FROM \n" +
            "    user u\n" +
            "JOIN \n" +
            "    friend_application fa ON u.id = fa.sender_id\n" +
            "JOIN \n" +
            "    user f ON fa.receiver_id = f.id\n" +
            "WHERE \n" +
            "    u.id = CASE WHEN ?1 != 0 THEN ?1 ELSE u.id END\n" +
            "    AND u.user_name LIKE CONCAT('%', ?2, '%')\n" +
            "    AND f.id IN (SELECT receiver_id FROM friend_application WHERE sender_id = ?3)", nativeQuery = true)
    public Page<FriendDTO> getFriendApplication(Integer id, String name, Integer uid, Pageable pageable);
}
