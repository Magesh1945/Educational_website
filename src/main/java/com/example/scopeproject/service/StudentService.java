package com.example.scopeproject.service;

import com.example.scopeproject.model.Student;
import com.example.scopeproject.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class StudentService {
   @Autowired
    StudentRepository studrepository;

     @Autowired
     private JavaMailSender mailSender;

     public  Student addStudent(Student student){
         return studrepository.save(student);
     }

     public void sendMail(String to,String sub,String body){
         SimpleMailMessage message=new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(sub);
            message.setText(body);
            message.setFrom("mageshselvaraj3@gmail.com");

            mailSender.send(message);

     }
}
