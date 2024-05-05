package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.UserGameRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGameRecordDao extends JpaRepository<UserGameRecord, Integer> {

}
