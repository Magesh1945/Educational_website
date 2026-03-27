package com.example.scopeproject.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class Otpservice {

    public String  generateOtp(){
        Random r=new Random();
        int otp=1000+ r.nextInt(9000);
        return String.valueOf(otp);
    }

}
