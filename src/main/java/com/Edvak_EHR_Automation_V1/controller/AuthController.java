package com.Edvak_EHR_Automation_V1.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.Edvak_EHR_Automation_V1.service.DataFetchService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final RestTemplate restTemplate;
    private final DataFetchService dataFetchService;

    public AuthController(RestTemplate restTemplate, DataFetchService dataFetchService) {
        this.restTemplate = restTemplate;
        this.dataFetchService = dataFetchService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";  // Show login page
    }

    @PostMapping("/login")
public ModelAndView login(@RequestParam String username, @RequestParam String password, HttpSession session) {
    ModelAndView mav = new ModelAndView();
    String loginUrl = "https://darwinapi.edvak.com:3000/users/login/";

    // Prepare headers
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.ALL)); // Accept: */*
    headers.add("User-Agent", "PostmanRuntime/7.43.0");
    headers.add("Cache-Control", "no-cache");

    // Prepare request payload
    Map<String, Object> loginPayload = new HashMap<>();
    loginPayload.put("email", username);
    loginPayload.put("password", password);
    loginPayload.put("loginWithOTP", false);

    ObjectMapper objectMapper = new ObjectMapper();
    String jsonPayload = "";

    try {
        jsonPayload = objectMapper.writeValueAsString(loginPayload);
    } catch (Exception e) {
        e.printStackTrace();
    }

    // Prepare the HTTP entity
    HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

    try {
        System.out.println("🔹 Sending API Request to: " + loginUrl);
        System.out.println("🔹 Request Payload: " + jsonPayload);

        ResponseEntity<Map> response = restTemplate.exchange(loginUrl, HttpMethod.POST, request, Map.class);

        System.out.println("✅ API Response: " + response);
        System.out.println("✅ Response Body: " + response.getBody());

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> result = (Map<String, Object>) response.getBody().get("result");

            if (result != null) {
                // ✅ Navigate deeper to userData ➡ practiceInformation ➡ p_id
                Map<String, Object> userData = (Map<String, Object>) result.get("userData");

                if (userData != null) {
                    Map<String, Object> practiceInformation = (Map<String, Object>) userData.get("practiceInformation");

                    if (practiceInformation != null && practiceInformation.containsKey("p_id")) {
                        String p_id = (String) practiceInformation.get("p_id");

                        System.out.println("✅ Extracted p_id: " + p_id);

                        // Store in session
                        session.setAttribute("p_id", p_id);
                        session.setAttribute("user", username);

                        return new ModelAndView("redirect:/dashboard");
                    } else {
                        System.err.println("❌ p_id not found in practiceInformation.");
                        mav.addObject("error", "❌ Login failed! p_id not found.");
                    }
                } else {
                    System.err.println("❌ userData not found in result.");
                    mav.addObject("error", "❌ Login failed! userData not found.");
                }
            } else {
                System.err.println("❌ result is null.");
                mav.addObject("error", "❌ Login failed! Invalid response.");
            }
        } else {
            System.err.println("❌ Login failed! Status: " + response.getStatusCode());
            mav.addObject("error", "❌ Login failed! Check your credentials.");
        }

    } catch (HttpClientErrorException e) {
        System.err.println("❌ HTTP Error: " + e.getStatusCode());
        System.err.println("❌ Response Body: " + e.getResponseBodyAsString());
        mav.addObject("error", "❌ Login Failed! " + e.getStatusCode());

    } catch (Exception e) {
        System.err.println("❌ General Error: " + e.getMessage());
        mav.addObject("error", "❌ Login Failed! Please try again.");
    }

    mav.setViewName("login");
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
