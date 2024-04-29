package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.FriendApplication;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendApplicationDao extends JpaRepository<FriendApplication, Integer> {
    @Query(value = "select id, user_name as userName, avatar from user where \n" +
            "    (\n" +
            "        id = CASE WHEN ?1 != 0 THEN ?1 ELSE id END \n" +
            "        AND \n" +
            "        user_name LIKE CONCAT('%', ?2, '%')\n" +
            "    )\n" +
            "    AND id in (select sender_id from friend_application where receiver_id = ?3)", nativeQuery = true)
    public Page<UserInfoDTO> getFriendApplication(Integer id, String name, Integer uid, Pageable pageable);
}
