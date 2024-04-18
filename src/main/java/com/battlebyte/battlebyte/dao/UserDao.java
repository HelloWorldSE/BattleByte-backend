package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    @Query(value = "select distinct * from user where user_name = ?1 and password = ?2", nativeQuery = true)
    public User findUser(String username, String password);

    @Query(value = "select distinct * from user where user_name = ?1", nativeQuery = true)
    public User findUserByName(String username);

//    @Query(value = "select distinct name from user_role, role where user_role.rid = role.rid and user_role.uid = ?1", nativeQuery = true)
//    public List<String> getRole(Integer uid);
//
//    @Query(value = "select distinct permission.name from user_role, role, role_permission, permission " +
//            "where user_role.rid = role.rid and role.rid = role_permission.rid and role_permission.pid = permission.pid " +
//            "and user_role.uid = ?1", nativeQuery = true)
//    public List<String> getPermission(Integer uid);

}
