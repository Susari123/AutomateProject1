package com.Edvak_EHR_Automation_V1.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

@Controller
public class OutputController {

    private final TestRunnerController testRunnerController;
    private final SimpMessagingTemplate messagingTemplate;

    // Store test execution history
    private final List<String> testLogs = new ArrayList<>();

    public OutputController(TestRunnerController testRunnerController, SimpMessagingTemplate messagingTemplate) {
        this.testRunnerController = testRunnerController;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Displays the last test output page.
     */
    @GetMapping("/viewOutput")
    public ModelAndView viewOutput(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelAndView mav = new ModelAndView("output");
        String lastTestOutput = testRunnerController.getLastTestOutput();

        if (lastTestOutput == null || lastTestOutput.isBlank()) {
            mav.addObject("consoleOutput", null);
        } else {
            mav.addObject("consoleOutput", lastTestOutput);
            mav.addObject("consoleOutputTrimmed", lastTestOutput.contains("...[Output trimmed]"));

            // Store test log for history
            synchronized (testLogs) {
                testLogs.add(lastTestOutput);
                if (testLogs.size() > 10) { // Keep last 10 logs
                    testLogs.remove(0);
                }
            }
        }

        return mav;
    }

    /**
     * WebSocket API to get real-time test output updates.
     */
    @GetMapping("/viewOutputData")
    @ResponseBody
    public String getConsoleOutput(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "Unauthorized";
        }

        String lastTestOutput = testRunnerController.getLastTestOutput();
        return (lastTestOutput == null || lastTestOutput.isBlank()) ? "No output available" : lastTestOutput;
    }

    /**
     * Clears the console output and notifies WebSocket clients.
     */
    @GetMapping("/clearOutput")
    @ResponseBody
    public ResponseEntity<String> clearConsoleOutput(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        // Clear the last test output
        testRunnerController.clearLastTestOutput();

        // Notify all WebSocket clients that logs are cleared
        messagingTemplate.convertAndSend("/topic/logs", "🚫 Test has not been started yet!");

        return ResponseEntity.ok("Output cleared successfully");
    }

    /**
     * Sends real-time log updates to connected WebSocket clients.
     */
    public void sendLogUpdate(String logData) {
        messagingTemplate.convertAndSend("/topic/logs", logData);
    }

    /**
     * API to download the last test output as a file.
     */
    @GetMapping("/downloadLog")
    public ResponseEntity<Resource> downloadLog(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String lastTestOutput = testRunnerController.getLastTestOutput();
        if (lastTestOutput == null || lastTestOutput.isBlank()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        ByteArrayResource resource = new ByteArrayResource(lastTestOutput.getBytes(StandardCharsets.UTF_8));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test_output.log")
                .body(resource);
    }

    /**
     * API to get the test execution history.
     */
    @GetMapping("/testHistory")
    public ModelAndView getTestHistory(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelAndView mav = new ModelAndView("history");
        mav.addObject("executions", testLogs);
        return mav;
    }
}
