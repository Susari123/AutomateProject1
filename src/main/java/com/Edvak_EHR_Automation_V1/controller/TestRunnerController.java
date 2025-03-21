package com.Edvak_EHR_Automation_V1.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.testng.TestNG;
import org.testng.internal.IResultListener;

import com.Edvak_EHR_Automation_V1.service.BuildCombinedJson;
import com.Edvak_EHR_Automation_V1.service.DataFetchService;
import com.Edvak_EHR_Automation_V1.service.EmailService;
import com.Edvak_EHR_Automation_V1.service.Patientfirstname;
import com.Edvak_EHR_Automation_V1.service.SessionData;

import jakarta.servlet.http.HttpSession;

@SuppressWarnings("unused")
@Controller
@RequestMapping("/api/test")
public class TestRunnerController {

    @SuppressWarnings("unused")
    // private static final Logger logger = LoggerFactory.getLogger(TestRunnerController.class);
    private static final int OUTPUT_LIMIT = 50000;

    public static Logger getLogger() {
        return logger;
    }

    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;
    private final DataFetchService dataFetchService;
    private final Deque<String> logsList = new ConcurrentLinkedDeque<>();

    private volatile String lastTestOutput = "";
    private volatile boolean isTestRunning = false;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final RestTemplate restTemplate = new RestTemplate();
    
    public TestRunnerController(SimpMessagingTemplate messagingTemplate,
                                EmailService emailService,
                                DataFetchService dataFetchService) {
        this.messagingTemplate = messagingTemplate;
        this.emailService = emailService;
        this.dataFetchService = dataFetchService;
    }
    private static final Logger logger = LoggerFactory.getLogger(TestRunnerController.class);
    
    // Initialize the Patient API class
    private final Patientfirstname patientService = new Patientfirstname();
    private final BuildCombinedJson combinedService = new BuildCombinedJson();
    
    @GetMapping("/login")
    public ModelAndView loginPage() {
        return new ModelAndView("login");
    }

    @PostMapping("/login")
public ModelAndView login(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session) {

    ModelAndView mav = new ModelAndView();
    String loginUrl = "https://darwinapi.edvak.com:3000/users/login/";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    // Login payload
    Map<String, Object> loginPayload = new HashMap<>();
    loginPayload.put("email", username);
    loginPayload.put("password", password);
    loginPayload.put("loginWithOTP", false);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(loginPayload, headers);

    try {
        logger.info("🔐 Sending login request to Darwin API");

        ResponseEntity<Map> response = new RestTemplate().exchange(loginUrl, HttpMethod.POST, request, Map.class);

        logger.info("✅ Raw API Response: {}", response.getBody()); // Print Full API Response

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> result = (Map<String, Object>) response.getBody().get("result");

            if (result != null) {
                String token = (String) result.get("token");
                logger.info("✅ Extracted Token: {}", token);

                Map<String, Object> userPreference = (Map<String, Object>) result.get("userPreference");
                if (userPreference == null) {
                    logger.error("❌ Login failed! userPreference is missing.");
                    mav.addObject("error", "❌ Login failed! Invalid response: userPreference missing.");
                    mav.setViewName("login");
                    return mav;
                }

                String defaultLocation = (String) userPreference.getOrDefault("defaultLocation", "N/A");
                String defaultLocationTimeZone = (String) userPreference.getOrDefault("defaultLocationTimeZone", "N/A");

                session.setAttribute("token", token);
                session.setAttribute("defaultLocation", defaultLocation);
                session.setAttribute("defaultLocationTimeZone", defaultLocationTimeZone);
                session.setAttribute("user", username);
                session.setAttribute("userPassword", password);

                SessionData.setUserCredentials(username, password);
                logger.info("✅ Login Successful. Token: {}", token);
                logger.info("📍 Default Location: {}", defaultLocation);
                logger.info("⏳ Time Zone: {}", defaultLocationTimeZone);

                // 🔄 Automatically trigger the Patient API class
                logger.info("🔄 Running Patient API to fetch patient details...");
                patientService.testGetPatientData(token, defaultLocation, defaultLocationTimeZone);

                // ✅ Automatically generate Combined JSON after successful login & data fetch
                logger.info("🔄 Generating Combined JSON data...");
                combinedService.buildCombinedJson();  // <-- THIS LINE TRIGGERS JSON GENERATION
                logger.info("✅ Combined JSON successfully generated.");

                return new ModelAndView("redirect:/api/test/");
            } else {
                logger.error("❌ Login failed! 'result' is missing in response.");
            }
        } else {
            logger.error("❌ Login failed! Unexpected response: {}", response.getStatusCode());
        }

    } catch (Exception e) {
        logger.error("❌ Error during login: {}", e.getMessage(), e);
        mav.addObject("error", "❌ Login failed! Unexpected error.");
    }

    mav.setViewName("login");
    return mav;
}


    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/api/test/login";
    }

    // ========================= DASHBOARD ==============================

    @GetMapping("/")
    public ModelAndView home(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/api/test/login");
        }
        return new ModelAndView("index");
    }

    // ========================= RUN TEST ==============================

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
        PrintStream customOut = new PrintStream(outputStream, true);
        System.setOut(customOut);

        boolean testFailed = false;

        try {
            logger.info("🚀 Executing TestNG Suite: {}", suiteFile);

            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList(suiteFile));

            TestResultListener resultListener = new TestResultListener();
            testng.addListener((IResultListener) resultListener);

            Thread logThread = startLogStreamingThread(outputStream);
            testng.run();
            logThread.interrupt();

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

        sendLogUpdate(lastTestOutput);
        logger.info("✅ Final logs sent to WebSocket!");

        // ✅ Send email notification after test execution
        String emailRecipient = "recipient@example.com"; // Change this
        String emailSubject = testFailed ? "❌ Test Execution Failed" : "✅ Test Execution Passed";
        String emailBody = "Dear Team,\n\nThe Selenium test execution has completed.\n"
                + "Test Status: " + (testFailed ? "❌ Failed" : "✅ Passed")
                + "\n\nBest Regards,\nAutomation Team";

        emailService.sendEmail(emailRecipient, emailSubject, emailBody);

        mav.addObject("message", testFailed ? "❌ Test Execution Failed!" : "✅ Test Suite Executed Successfully!");
        mav.addObject("showOutputButton", true);
        return mav;
    }

    private Thread startLogStreamingThread(ByteArrayOutputStream outputStream) {
        Thread logThread = new Thread(() -> {
            while (isTestRunning) {
                String logs = outputStream.toString().trim();
                if (!logs.isBlank()) {
                    sendLogUpdate(logs);
                    outputStream.reset();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        logThread.start();
        return logThread;
    }

    // ========================= LOG HANDLERS ==============================

    public synchronized String getLastTestOutput() {
        return lastTestOutput;
    }

    public synchronized void clearLastTestOutput() {
        lastTestOutput = "";
        logsList.clear();
        sendLogUpdate("🚫 Test has not been started yet!");
    }

    public void sendLogUpdate(String logData) {
        if (logData != null && !logData.isBlank()) {
            logsList.add(logData);
            if (logsList.size() > 1000) {
                logsList.pollFirst(); // ✅ Prevent memory overflow
            }
            messagingTemplate.convertAndSend("/topic/logs", logData.getBytes(StandardCharsets.UTF_8));
            logger.info("🔥 WebSocket Sent Log: {}", logData);
        }
    }

    @GetMapping("/logs/previous")
    @ResponseBody
    public String getPreviousLogs() {
        if (logsList.isEmpty()) {
            return "🚫 No logs available!";
        }
        return String.join("\n", logsList);
    }
}
