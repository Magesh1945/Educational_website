package com.example.scopeproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class Otpmailservice {

    @Autowired
    private JavaMailSender mailSender;

    public void sentOtp(String to, String sub,String body){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(sub);
        message.setText(body);
        message.setFrom("mageshselvaraj3@gmail.com");
        mailSender.send(message);
    }
}
