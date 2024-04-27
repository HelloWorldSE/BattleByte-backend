package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.UserGameRecordDao;
import com.battlebyte.battlebyte.entity.UserGameRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class UserGameRecordService {
    @Autowired
    public UserGameRecordDao userGameRecordDao;

    public void save(UserGameRecord userGameRecord) {
        userGameRecordDao.save(userGameRecord);
    }

    public void del(Integer id) {
        userGameRecordDao.deleteById(id);
    }
}
