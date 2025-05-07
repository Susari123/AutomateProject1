package com.Edvak_EHR_Automation_V1.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.client.HttpClientErrorException;
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
        headers.add("User-Agent", "PostmanRuntime/7.43.0");
        headers.add("Cache-Control", "no-cache");
    
        Map<String, Object> loginPayload = new HashMap<>();
        loginPayload.put("email", username);
        loginPayload.put("password", password);
        loginPayload.put("loginWithOTP", false);
    
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(loginPayload, headers);
    
        try {
            logger.info("🔐 Sending login request...");
            ResponseEntity<Map> response = new RestTemplate().exchange(loginUrl, HttpMethod.POST, request, Map.class);
            logger.info("🌐 Login response body: {}", response.getBody());
    
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Map<String, Object> result = (Map<String, Object>) body.get("result");
    
                if (result == null) {
                    logger.warn("⚠️ 'result' key not found in login response: {}", body);
                    mav.addObject("error", "Login failed! No result returned.");
                    mav.setViewName("login");
                    return mav;
                }
    
                String token = (String) result.get("token");
                session.setAttribute("token", token);
                session.setAttribute("user", username);
                session.setAttribute("userPassword", password);
                SessionData.setUserCredentials(username, password);
    
                // Check for multiple practices
                List<Map<String, Object>> practicesList = (List<Map<String, Object>>) result.get("practices");
                if (practicesList != null && !practicesList.isEmpty()) {
                    logger.info("🔄 Multiple practices found. Redirecting to selection page.");
                    session.setAttribute("rawLoginResult", result);
                    ModelAndView selectMav = new ModelAndView("selectPractice");
                    selectMav.addObject("practices", practicesList);
                    return selectMav;
                }
    
                // Handle single practice from userData.practice
                Map<String, Object> userData = (Map<String, Object>) result.get("userData");
                if (userData != null) {
                    List<Map<String, Object>> singlePracticeList = (List<Map<String, Object>>) userData.get("practice");
                    if (singlePracticeList != null && !singlePracticeList.isEmpty()) {
                        Map<String, Object> practice = singlePracticeList.get(0);
                        String p_id = (String) practice.get("_id");
    
                        session.setAttribute("p_id", p_id);
    
                        Map<String, Object> userPreference = (Map<String, Object>) result.get("userPreference");
                        String defaultLocation = userPreference != null ? (String) userPreference.getOrDefault("defaultLocation", "N/A") : "N/A";
                        String defaultLocationTimeZone = userPreference != null ? (String) userPreference.getOrDefault("deafaultLocationTimeZone", "N/A") : "N/A";
    
                        session.setAttribute("defaultLocation", defaultLocation);
                        session.setAttribute("defaultLocationTimeZone", defaultLocationTimeZone);
    
                        fetchAndStoreClearingHouseInfo(p_id, session);
                        patientService.testGetPatientData(token, defaultLocation, defaultLocationTimeZone);
                        String clearingHouseKey = (String) session.getAttribute("clearing_house_key");
                        combinedService.buildCombinedJson(clearingHouseKey);
    
                        logger.info("✅ Login Success with single practice.");
                        return new ModelAndView("redirect:/api/test/");
                    }
                }
    
                logger.error("❌ No practice data found in login response.");
                mav.addObject("error", "Login failed! No practice information found.");
            } else {
                mav.addObject("error", "Login failed! Invalid credentials or unexpected response.");
            }
    
        } catch (HttpClientErrorException e) {
            logger.error("❌ HTTP Error: {}", e.getStatusCode());
            logger.error("❌ Body: {}", e.getResponseBodyAsString());
            mav.addObject("error", "Login failed! " + e.getStatusCode());
        } catch (Exception e) {
            logger.error("❌ General Exception: {}", e.getMessage(), e);
            mav.addObject("error", "Login failed due to unexpected error.");
        }
    
        mav.setViewName("login");
        return mav;
    }
    

    @PostMapping("/selectPractice")
    public ModelAndView selectPractice(@RequestParam String selectedPracticeId, HttpSession session) {
        try {
            Map<String, Object> result = (Map<String, Object>) session.getAttribute("rawLoginResult");
    
            if (result == null) {
                return new ModelAndView("login", Map.of("error", "Session expired. Please login again."));
            }
    
            session.setAttribute("p_id", selectedPracticeId);
    
            Map<String, Object> userPreference = (Map<String, Object>) result.get("userPreference");
            String defaultLocation = userPreference != null ? (String) userPreference.getOrDefault("defaultLocation", "N/A") : "N/A";
            String defaultLocationTimeZone = userPreference != null ? (String) userPreference.getOrDefault("deafaultLocationTimeZone", "N/A") : "N/A";
    
            session.setAttribute("defaultLocation", defaultLocation);
            session.setAttribute("defaultLocationTimeZone", defaultLocationTimeZone);
    
            String token = (String) session.getAttribute("token");
            fetchAndStoreClearingHouseInfo(selectedPracticeId, session);
            patientService.testGetPatientData(token, defaultLocation, defaultLocationTimeZone);
            String clearingHouseKey = (String) session.getAttribute("clearing_house_key");
            combinedService.buildCombinedJson(clearingHouseKey);
    
            logger.info("✅ Practice selected and login completed.");
            return new ModelAndView("redirect:/api/test/");
        } catch (Exception e) {
            logger.error("❌ Error during practice selection: {}", e.getMessage(), e);
            ModelAndView mav = new ModelAndView("selectPractice");
            mav.addObject("error", "Something went wrong. Please try again.");
            return mav;
        }
    }
    

    
    
    private void fetchAndStoreClearingHouseInfo(String p_id, HttpSession session) {
        String apiUrl = "https://darwinapi.edvak.com:3000/practice-settings/getPracticeSetting/" + p_id;
    
        try {
            String token = (String) session.getAttribute("token");
            if (token == null || token.isEmpty()) {
                logger.error("❌ Token is missing from session.");
                return;
            }
    
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("Authorization", "Bearer " + token);
            headers.set("p_id", p_id);
            headers.set("Origin", "https://darwinapi.edvak.com");
            headers.set("Referer", "https://darwinapi.edvak.com/");
            headers.set("User-Agent", "Mozilla/5.0");
            headers.set("Sec-Fetch-Dest", "empty");
            headers.set("Sec-Fetch-Mode", "cors");
            headers.set("Sec-Fetch-Site", "same-site");
            headers.set("sec-ch-ua", "\"Chromium\";v=\"134\"");
            headers.set("sec-ch-ua-mobile", "?0");
            headers.set("sec-ch-ua-platform", "\"Windows\"");
    
            HttpEntity<String> entity = new HttpEntity<>(headers);
    
            logger.info("🔹 Fetching practice settings: {}", apiUrl);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
    
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Object resultObj = response.getBody().get("result");
    
                if (resultObj instanceof Map) {
                    Map<String, Object> result = (Map<String, Object>) resultObj;
                    String clearingHouseKey = (String) result.get("clearing_house_key");
                    String clearingHouseName = (String) result.get("clearing_house_name");
    
                    if (clearingHouseKey != null) {
                        session.setAttribute("clearing_house_key", clearingHouseKey);
                        logger.info("✅ Stored clearing_house_key: {}", clearingHouseKey);
                    } else {
                        logger.warn("⚠️ 'clearing_house_key' not found in result.");
                    }
    
                    if (clearingHouseName != null) {
                        session.setAttribute("clearing_house_name", clearingHouseName);
                        logger.info("✅ Stored clearing_house_name: {}", clearingHouseName);
                    } else {
                        logger.warn("⚠️ 'clearing_house_name' not found in result.");
                    }
                } else {
                    logger.warn("⚠️ Unexpected result structure: {}", resultObj);
                }
            } else {
                logger.error("❌ Failed to fetch settings. Status: {}", response.getStatusCode());
            }
    
        } catch (Exception e) {
            logger.error("❌ Error fetching practice settings: {}", e.getMessage(), e);
        }
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
