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
import org.testng.internal.IResultListener;

import jakarta.servlet.http.HttpSession;

@Controller
public class TestRunnerController {

    private static final Logger logger = LoggerFactory.getLogger(TestRunnerController.class);
    private static final int OUTPUT_LIMIT = 50000;
    private String lastTestOutput = "";

    @GetMapping("/")
    public ModelAndView home(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/login");
        }
        return new ModelAndView("index");
    }

    @GetMapping("/runTest")
    public ModelAndView runTest(@RequestParam String suiteFile,
                                @RequestParam(required = false) Integer taskCount,
                                HttpSession session) {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelAndView mav = new ModelAndView("index");

        if (suiteFile == null || suiteFile.isBlank()) {
            mav.addObject("message", "❌ No TestNG Suite File Provided!");
            return mav;
        }

        if (!suiteFile.matches("^(PaymentTestNG\\.xml|LoginTestNG\\.xml|BillingGenerateClaimTestNG\\.xml|ErabillingTestNG\\.xml|TC_RefundTestNG\\.xml)$")) {
            mav.addObject("message", "❌ Invalid Suite File Selection!");
            return mav;
        }

        // If BillingGenerateClaimTestNG.xml is selected, ensure that taskCount is provided.
        if ("BillingGenerateClaimTestNG.xml".equals(suiteFile)) {
            if (taskCount == null) {
                mav.addObject("message", "❌ Please provide the number of tasks for BillingGenerateClaimTestNG.xml!");
                return mav;
            }
            // Pass the task count to the test via a system property.
            System.setProperty("taskCount", String.valueOf(taskCount));
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        boolean testFailed = false;

        try {
            logger.info("Executing TestNG Suite: {}", suiteFile);

            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList(suiteFile));

            // Track test results using a listener.
            TestResultListener resultListener = new TestResultListener();
            testng.addListener((IResultListener) resultListener);

            testng.run();

            // Check if tests failed.
            if (resultListener.hasFailures()) {
                testFailed = true;
            }

        } catch (Throwable e) {
            logger.error("Test Execution Failed: {}", e.getMessage(), e);
            testFailed = true;
        } finally {
            System.setOut(originalOut);
        }

        lastTestOutput = outputStream.toString();

        if (lastTestOutput.length() > OUTPUT_LIMIT) {
            lastTestOutput = lastTestOutput.substring(0, OUTPUT_LIMIT) + "\n...[Output trimmed]";
            logger.warn("Console output was trimmed because it exceeded {} characters.", OUTPUT_LIMIT);
        }

        // Set the success or failure message based on test results.
        if (testFailed) {
            mav.addObject("message", "❌ Test Execution Failed!");
        } else {
            mav.addObject("message", "✅ Test Suite Executed Successfully: " + suiteFile);
        }

        mav.addObject("showOutputButton", true);
        return mav;
    }

    public String getLastTestOutput() {
        return lastTestOutput;
    }
}
