package com.Edvak_EHR_Automation_V1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    // Dummy credentials for login (Replace with database check in real apps)
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "password123";

    @GetMapping("/login")
    public String loginPage() {
        return "login";  // Show login page
    }

    @PostMapping("/login")
    public ModelAndView login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        ModelAndView mav = new ModelAndView();

        if (VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password)) {
            session.setAttribute("user", username); // Store session
            mav.setViewName("redirect:/"); // Redirect to home (Test Runner)
        } else {
            mav.setViewName("login");
            mav.addObject("error", "Invalid username or password.");
        }
        return mav;
    }
    @GetMapping("/logout")
    public ModelAndView logout(HttpSession session) {
        session.invalidate();
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("logoutSuccess", "✅ You have been logged out successfully!");
        return mav;
    }
    
}