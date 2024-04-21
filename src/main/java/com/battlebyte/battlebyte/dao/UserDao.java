package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.entity.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    @Query(value = "select distinct * from user where user_name = ?1 and password = ?2", nativeQuery = true)
    public User findUser(String username, String password);

    @Query(value = "insert into user_role (uid, rid) VALUE (?1, ?2)", nativeQuery = true)
    public void setRole(Integer uid, Integer rid);

    public User findByUserName(String username);

    @Query(value = "select id, user_name, user_email, avatar from user where id = ?1", nativeQuery = true)
    public User findByUserId(Integer id);

    @Query(value = "select distinct name from user_role, role where user_role.rid = role.rid and user_role.uid = ?1", nativeQuery = true)
    public List<String> getRole(Integer uid);

    @Query(value = "select distinct permission.name from user_role, role, role_permission, permission " +
            "where user_role.rid = role.rid and role.rid = role_permission.rid and role_permission.pid = permission.pid " +
            "and user_role.uid = ?1", nativeQuery = true)
    public List<String> getPermission(Integer uid);

    @Query(value = "select id, user_name from user where id in (\n" +
            "    select large_id from friend where (small_id = ?1)\n" +
            "        union\n" +
            "    select small_id from friend where (large_id = ?1)\n" +
            "    )", nativeQuery = true)
    public Page<UserDTO> findFriend(Integer uid, Pageable pageable);

}
