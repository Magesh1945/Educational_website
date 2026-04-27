package com.example.scopeproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.scopeproject.model.Student;
import com.example.scopeproject.repository.StudentRepository;
import com.example.scopeproject.service.Otpmailservice;
import com.example.scopeproject.service.Otpservice;
import com.example.scopeproject.service.StudentService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

  @Autowired
  private StudentService studentservice;
  @Autowired
  private StudentRepository studentRep;
  @Autowired
  private Otpservice otpservice;
  @Autowired
  private Otpmailservice otpmailservice;

    // @GetMapping("/dash")
    // public String dashb(){
    //     return "dashboard";
    // }
    
    @GetMapping("/home")
    public String home(){
        return "home";
    }
     
    @GetMapping("/register")
    public String Register(Model m){
        m.addAttribute("student",new Student());
        return "register";
    }

    @GetMapping("/course")
    public String Course(){
       return "courses";
    }

    @GetMapping("/contact")
    public String Contact(){
       return "contact";
    }
     
    @GetMapping("/about")
    public String About(){
          return "about";
    }

    @PostMapping("/register")
    public String saveStudent(@ModelAttribute("student") Student student){
      studentservice.addStudent(student);
      studentservice.sendMail(
              student.getEmail(),
              "Registration Successful",
              "Hello "+student.getFullname()+" your registeration completed"
      );
      return "redirect:/register";
    }

//              <----- LOGIN ----->

  @GetMapping("/login")
  public String login(Model m){
      m.addAttribute("student",new Student());
      return "login";
  }

  @GetMapping("/sentotp")
  public  String sentotp(Model m){
      m.addAttribute("student",new Student());
      return "sentotp";
  }

  @PostMapping("/sentotp")
   public String sendOtp(@RequestParam String email, Model m, HttpSession session){
        Student existingStudent=studentRep.findByEmail(email);

        if (existingStudent!=null){
            String newotp=otpservice.generateOtp();
            existingStudent.setOtp(newotp);
            studentRep.save(existingStudent);
            otpmailservice.sentOtp(
                    existingStudent.getEmail(),
                    "Your OTP",
                    "Your OTP is "+existingStudent.getOtp()
            );
            session.setAttribute("email",existingStudent.getEmail());
            return "verify";
        }else{
            m.addAttribute("msg","Your are not registered ❌");
        }
            return "result";
  }

  @PostMapping("/verifyotp")
    public String verify(@RequestParam String otp,Model m,HttpSession session){

        String email= (String) session.getAttribute("email");
        Student verifyStudent=studentRep.findByEmail(email);

        if(verifyStudent!=null && verifyStudent.getOtp().equals(otp)){
            verifyStudent.setVerified(1);
            studentRep.save(verifyStudent);
//          session.removeAttribute("email");
            // ✅ mark first time login
            session.setAttribute("firstLogin", true);
            return "redirect:/login";

        } else {
            m.addAttribute("msg","Invalid OTP ❌");
            return "result";
        }

  }
                               //LOGIN

    @PostMapping("/submit")
    public String dashboard(@ModelAttribute Student student,
                        HttpSession session,
                        Model m){

        String email = student.getUsername(); // email
        String password = student.getPassword();

        Student dbUser = studentRep.findByEmail(email);

        // ✅ first time (after OTP)
        Boolean firstLogin = (Boolean) session.getAttribute("firstLogin");

        if(firstLogin != null && firstLogin){

            if(dbUser != null){

                // 👉 set password for first time
                dbUser.setPassword(password);
                dbUser.setUsername(email);
                studentRep.save(dbUser);

                session.removeAttribute("firstLogin");

              session.setAttribute("user", dbUser); // ✅ IMPORTANT

            return "redirect:/dashboard"; 
            }
        }

        // ✅ normal login
        if(dbUser != null && dbUser.getPassword() != null){

            if(dbUser.getPassword().equals(password)){

                session.setAttribute("user", dbUser);
               return "redirect:/dashboard";

            } else {
                m.addAttribute("msg","Wrong Password ❌");
                return "result";
            }
        }

        m.addAttribute("msg","User not found ❌");
        return "result";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session,
                         HttpServletRequest request,
                         HttpServletResponse response) {

        // 🔴 1. Invalidate session
        session.invalidate();

        // 🔴 2. Remove all cookies
        Cookie[] cookies = request.getCookies();

        if(cookies != null){
            for(Cookie cookie : cookies){
                cookie.setValue(null);
                cookie.setPath("/");
                cookie.setMaxAge(0); // delete cookie
                response.addCookie(cookie);
            }
        }

        return "redirect:/register"; // go to login page
    }

  @GetMapping("/dashboard")
 public String dashboard(HttpSession session, Model model) {

    Student user = (Student) session.getAttribute("user");

    if (user == null) {
        return "redirect:/login"; // 🚫 block access after logout
    }

    model.addAttribute("user", user);

    return "dashboard";
}

@GetMapping("/editProfile")
public String editProfile(HttpSession session, Model model) {

    Student user = (Student) session.getAttribute("user");

    if (user == null) {
        return "redirect:/login";   // 🚫 no session
    }

    model.addAttribute("user", user);   // ✅ MUST

    return "editProfile";
}
      

     @PostMapping("/updateProfile")
          public String updateProfile(@RequestParam String fullname,
                            @RequestParam String email,
                            @RequestParam String mobilenum,
                            @RequestParam String eductionqualification,
                            @RequestParam String city,
                            HttpSession session) {

    Student user = (Student) session.getAttribute("user"); // ✅ use "user"

    if(user == null){
        return "redirect:/login";
    }

    user.setFullname(fullname);
    user.setEmail(email);
    user.setMobilenum(mobilenum);
    user.setEductionqualification(eductionqualification);
    user.setCity(city);

    studentRep.save(user);

    session.setAttribute("user", user); // ✅ update session

    return "redirect:/dashboard";
}
}
