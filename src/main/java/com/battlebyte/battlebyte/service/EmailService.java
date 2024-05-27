package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.FindPasswordDao;
import com.battlebyte.battlebyte.entity.FindPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private FindPasswordDao findPasswordDao;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);
    }

    public void sendPasswordEmail(Integer id, String to) {
        SecureRandom secureRandom = new SecureRandom();
        int verifyNumber = 100000 + secureRandom.nextInt(900000);
        findPasswordDao.save(new FindPassword(id, verifyNumber, new Date()));
        String text = "您正在找回密码，验证码是：" + verifyNumber + "，验证码10分钟后失效！";
        sendEmail(to, "找回密码", text);
    }
}
 