package com.Edvak_EHR_Automation_V1.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.testng.TestNG;

@Controller
public class TestRunnerController {

    private static final Logger logger = LoggerFactory.getLogger(TestRunnerController.class);

    private static final int OUTPUT_LIMIT = 50000; // Trim limit (characters)
    private String lastTestOutput = "";

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/runTest")
    public ModelAndView runTest(@RequestParam String suiteFile) {
        ModelAndView mav = new ModelAndView("index");

        if (suiteFile == null || suiteFile.isBlank()) {
            mav.addObject("message", "❌ No TestNG Suite File Provided!");
            return mav;
        }

        // Optional: Validate allowed files only
        if (!suiteFile.matches("^(PaymentTestNG\\.xml|LoginTestNG\\.xml|BillingGenerateClaimTestNG\\.xml)$")) {
            mav.addObject("message", "❌ Invalid Suite File Selection!");
            return mav;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            logger.info("Executing TestNG Suite: {}", suiteFile);

            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList(suiteFile));
            testng.run();

            mav.addObject("message", "✅ Test Suite Executed Successfully: " + suiteFile);
        } catch (Throwable e) { // Catch any error (not just RuntimeException)
            logger.error("Test Execution Failed: {}", e.getMessage(), e);
            mav.addObject("message", "❌ Test Execution Failed: " + e.getMessage());
        } finally {
            System.setOut(originalOut);
        }

        lastTestOutput = outputStream.toString();

        // Optional: Trim Output if Too Long
        if (lastTestOutput.length() > OUTPUT_LIMIT) {
            lastTestOutput = lastTestOutput.substring(0, OUTPUT_LIMIT) + "\n...[Output trimmed]";
            logger.warn("Console output was trimmed because it exceeded {} characters.", OUTPUT_LIMIT);
        }

        mav.addObject("showOutputButton", true);
        return mav;
    }

    @GetMapping("/viewOutput")
    public ModelAndView viewOutput() {
        ModelAndView mav = new ModelAndView("output");

        if (lastTestOutput == null || lastTestOutput.isBlank()) {
            mav.addObject("consoleOutput", null);
        } else {
            mav.addObject("consoleOutput", lastTestOutput);
            mav.addObject("consoleOutputTrimmed", lastTestOutput.contains("...[Output trimmed]"));
        }

        return mav;
    }
}
