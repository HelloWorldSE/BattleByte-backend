package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.entity.dto.FriendDTO;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import com.battlebyte.battlebyte.entity.dto.UserProfileDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    @Query(value = "select distinct id, avatar, user_name as userName, user_email as userEmail from user where id = CASE WHEN ?1 != 0 THEN ?1 ELSE id END and user_name LIKE CONCAT('%', ?2, '%')", nativeQuery = true)
    public Page<UserInfoDTO> findUser(Integer id, String name, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "insert into user_role (uid, rid) VALUE (?1, ?2)", nativeQuery = true)
    public void setRole(Integer uid, Integer rid);

    public User findByUserName(String username);

    @Query(value = "select id, user_name as userName, user_email as userEmail, avatar, rating from user where id = ?1", nativeQuery = true)
    public UserProfileDTO findByUserId(Integer id);

    @Query(value = "select distinct name from user_role, role where user_role.rid = role.rid and user_role.uid = ?1", nativeQuery = true)
    public List<String> getRole(Integer uid);

    @Query(value = "select distinct permission.name from user_role, role, role_permission, permission " +
            "where user_role.rid = role.rid and role.rid = role_permission.rid and role_permission.pid = permission.pid " +
            "and user_role.uid = ?1", nativeQuery = true)
    public List<String> getPermission(Integer uid);

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
            "        large_id AS friendId\n" +
            "    FROM \n" +
            "        friend\n" +
            "    WHERE \n" +
            "        small_id = ?3\n" +
            "    UNION ALL\n" +
            "    SELECT \n" +
            "        small_id AS friendId\n" +
            "    FROM \n" +
            "        friend\n" +
            "    WHERE \n" +
            "        large_id = ?3\n" +
            ") f ON u.id = f.friendId\n" +
            "WHERE \n" +
            "    u.id = CASE WHEN ?1 != 0 THEN ?1 ELSE u.id END\n" +
            "    AND u.user_name LIKE CONCAT('%', ?2, '%');", nativeQuery = true)
    public Page<FriendDTO> findFriend(Integer id, String name, Integer uid, Pageable pageable);

}
