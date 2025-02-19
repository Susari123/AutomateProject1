package com.Edvak_EHR_Automation_V1.controller;

import java.util.Collections;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.testng.TestNG;

@Controller
public class TestRunnerController {

    @GetMapping("/")
    public String home() {
        return "index"; // Loads index.html from /resources/templates/
    }

    @GetMapping("/runTest")
    public ModelAndView runTest(@RequestParam String suiteFile) {
        ModelAndView mav = new ModelAndView("index");

        try {
            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList(suiteFile));
            testng.run();

            mav.addObject("message", "Test Suite Executed Successfully: " + suiteFile);
        } catch (Exception e) {
            mav.addObject("message", "Test Execution Failed: " + e.getMessage());
        }

        return mav;
    }
}
