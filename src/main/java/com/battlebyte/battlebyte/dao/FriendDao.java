package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.Friend;
import com.battlebyte.battlebyte.entity.dto.FriendDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendDao extends JpaRepository<Friend, Integer> {
    @Query(value = "SELECT * FROM friend WHERE small_id = ?1 AND large_id = ?2", nativeQuery = true)
    public Friend findFriendById(Integer smallId, Integer largeId);

    @Query(value = "SELECT \n" +
            "    u.id,\n" +
            "    u.user_name AS userName,\n" +
            "    u.avatar,\n" +
            "    u.user_email AS userEmail,\n" +
            "    f.friendId\n" +
            "FROM \n" +
            "    user u\n" +
            "JOIN (\n" +
            "    SELECT \n" +
            "        id as friendId, large_id AS id\n" +
            "    FROM \n" +
            "        friend\n" +
            "    WHERE \n" +
            "        small_id = ?3\n" +
            "    UNION ALL\n" +
            "    SELECT \n" +
            "        id as friendId, small_id AS id\n" +
            "    FROM \n" +
            "        friend\n" +
            "    WHERE \n" +
            "        large_id = ?3\n" +
            ") f ON u.id = f.id\n" +
            "WHERE \n" +
            "    u.id = CASE WHEN ?1 != 0 THEN ?1 ELSE u.id END\n" +
            "    AND u.user_name LIKE CONCAT('%', ?2, '%');", nativeQuery = true)
    public Page<FriendDTO> findFriend(Integer id, String name, Integer uid, Pageable pageable);
}
