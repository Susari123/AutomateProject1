package com.Edvak_EHR_Automation_V1.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.testng.TestNG;

@Controller
public class TestRunnerController {

    private String lastTestOutput = ""; // Store the latest output

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/runTest")
    public ModelAndView runTest(@RequestParam String suiteFile) {
        ModelAndView mav = new ModelAndView("index");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList(suiteFile));
            testng.run();

            mav.addObject("message", "Test Suite Executed Successfully: " + suiteFile);
        } catch (Exception e) {
            mav.addObject("message", "Test Execution Failed: " + e.getMessage());
        } finally {
            System.setOut(originalOut);
        }

        lastTestOutput = outputStream.toString(); // Store the output for later viewing
        mav.addObject("showOutputButton", true); // Show the button after running the test

        return mav;
    }

    @GetMapping("/viewOutput")
public ModelAndView viewOutput() {
    ModelAndView mav = new ModelAndView("output");

    if (lastTestOutput == null || lastTestOutput.isBlank()) {
        mav.addObject("consoleOutput", null); // Mark as null for "Test not started yet"
    } else {
        mav.addObject("consoleOutput", lastTestOutput);
    }

    return mav;
}

}
