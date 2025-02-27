package com.Edvak_EHR_Automation_V1.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate messagingTemplate;
    private volatile String lastTestOutput = "";
    private volatile boolean isTestRunning = false;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // For background log streaming

    public TestRunnerController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

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
    
        if (isTestRunning) {
            mav.addObject("message", "⚠️ A test is already running! Please wait.");
            return mav;
        }
    
        isTestRunning = true;
    
        // ✅ Set `taskCount` if the selected suite is `BillingGenerateClaimTestNG.xml`
        if ("BillingGenerateClaimTestNG.xml".equals(suiteFile)) {
            if (taskCount == null || taskCount <= 0) {
                mav.addObject("message", "⚠️ Please provide the number of tasks!");
                isTestRunning = false;
                return mav;
            }
            System.setProperty("taskCount", String.valueOf(taskCount));
            logger.info("🔢 Set taskCount: {}", taskCount);
        }
    
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream customOut = new PrintStream(outputStream, true); // ✅ Auto-flushing enabled
        System.setOut(customOut);
    
        boolean testFailed = false;
    
        try {
            logger.info("🚀 Executing TestNG Suite: {}", suiteFile);
    
            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList(suiteFile));
    
            TestResultListener resultListener = new TestResultListener();
            testng.addListener((IResultListener) resultListener);
    
            // ✅ Start log streaming thread in parallel
            Thread logThread = startLogStreamingThread(outputStream);
            testng.run();
            logThread.interrupt(); // ✅ Stop streaming after test completion
    
            if (resultListener.hasFailures()) {
                testFailed = true;
            }
    
        } catch (Throwable e) {
            logger.error("❌ Test Execution Failed: {}", e.getMessage(), e);
            testFailed = true;
        } finally {
            System.setOut(originalOut);
            customOut.close();
            isTestRunning = false;
        }
    
        lastTestOutput = outputStream.toString();
    
        if (lastTestOutput.length() > OUTPUT_LIMIT) {
            lastTestOutput = lastTestOutput.substring(0, OUTPUT_LIMIT) + "\n...[Output trimmed]";
            logger.warn("⚠️ Console output was trimmed because it exceeded {} characters.", OUTPUT_LIMIT);
        }
    
        // ✅ Send final logs after test completes
        sendLogUpdate(lastTestOutput);
        logger.info("✅ Final logs sent to WebSocket!");
    
        mav.addObject("message", testFailed ? "❌ Test Execution Failed!" : "✅ Test Suite Executed Successfully!");
        mav.addObject("showOutputButton", true);
        return mav;
    }
    
    /**
     * ✅ Streams logs in the background every second.
     */
    private Thread startLogStreamingThread(ByteArrayOutputStream outputStream) {
        Thread logThread = new Thread(() -> {
            while (isTestRunning) {
                String logs = outputStream.toString();
                if (!logs.isBlank()) {
                    sendLogUpdate(logs);
                    outputStream.reset();
                }
                try {
                    Thread.sleep(1000); // ✅ Send logs every second
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        logThread.start();
        return logThread;
    }
    
    public synchronized String getLastTestOutput() {
        return lastTestOutput;
    }

    public synchronized void clearLastTestOutput() {
        lastTestOutput = "";
        sendLogUpdate("🚫 Test has not been started yet!");
    }

    public void sendLogUpdate(String logData) {
        if (logData != null && !logData.isBlank()) {
            logger.info("🔥 WebSocket Sent Log: {}", logData);
            messagingTemplate.convertAndSend("/topic/logs", logData.getBytes(StandardCharsets.UTF_8)); // ✅ Encode properly
        } else {
            logger.warn("⚠️ No log data to send via WebSocket.");
        }
    }
}