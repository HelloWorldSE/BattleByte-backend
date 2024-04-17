package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    @Query(value = "select distinct * from user where user_name = ?1 and password = ?2", nativeQuery = true)
    public User findUser(String username, String password);

    @Query(value = "select distinct * from user where user_name = ?1", nativeQuery = true)
    public User findUserByName(String username);
}
