package com.Edvak_EHR_Automation_V1.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
    
        // Initialize headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "PostmanRuntime/7.43.0");
        headers.add("Cache-Control", "no-cache");
    
        // Prepare login payload
        Map<String, Object> loginPayload = new HashMap<>();
        loginPayload.put("email", username);
        loginPayload.put("password", password);
        loginPayload.put("loginWithOTP", false);
    
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(loginPayload, headers);
    
        try {
            logger.info("🔐 Sending login request to Darwin API...");
            logger.info("🔸 Login URL: {}", loginUrl);
            logger.info("🔸 Payload: {}", loginPayload);
    
            ResponseEntity<Map> response = new RestTemplate().exchange(loginUrl, HttpMethod.POST, request, Map.class);
    
            logger.info("✅ Raw API Response: {}", response.getBody());
    
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
    
                Map<String, Object> result = (Map<String, Object>) response.getBody().get("result");
    
                if (result != null) {
                    // ✅ Extract Token
                    String token = (String) result.get("token");
                    logger.info("✅ Extracted Token: {}", token);
    
                    // ✅ Extract userData ➡ practiceInformation ➡ p_id
                    Map<String, Object> userData = (Map<String, Object>) result.get("userData");
                    String p_id = "N/A";
    
                    if (userData != null) {
                        Map<String, Object> practiceInformation = (Map<String, Object>) userData.get("practiceInformation");
    
                        if (practiceInformation != null && practiceInformation.containsKey("p_id")) {
                            p_id = (String) practiceInformation.get("p_id");
                            logger.info("✅ Extracted p_id: {}", p_id);
                        } else {
                            logger.warn("⚠️ practiceInformation or p_id not found.");
                        }
                    } else {
                        logger.warn("⚠️ userData is missing in the result.");
                    }
    
                    // ✅ Extract user preferences
                    Map<String, Object> userPreference = (Map<String, Object>) result.get("userPreference");
                    String defaultLocation = "N/A";
                    String defaultLocationTimeZone = "N/A";
    
                    if (userPreference != null) {
                        defaultLocation = (String) userPreference.getOrDefault("defaultLocation", "N/A");
                        defaultLocationTimeZone = (String) userPreference.getOrDefault("defaultLocationTimeZone", "N/A");
    
                        logger.info("📍 Default Location: {}", defaultLocation);
                        logger.info("⏳ Time Zone: {}", defaultLocationTimeZone);
                    } else {
                        logger.warn("⚠️ userPreference is missing.");
                    }
    
                    // ✅ Store data in session
                    session.setAttribute("token", token);
                    session.setAttribute("defaultLocation", defaultLocation);
                    session.setAttribute("defaultLocationTimeZone", defaultLocationTimeZone);
                    session.setAttribute("p_id", p_id);
                    session.setAttribute("user", username);
                    session.setAttribute("userPassword", password);
    
                    // ✅ Persist credentials (if required elsewhere)
                    SessionData.setUserCredentials(username, password);
    
                    // ✅ Fetch & store clearing house info (this is where your method goes)
                    logger.info("🔄 Fetching and storing clearing house info...");
                    fetchAndStoreClearingHouseInfo(p_id, session);
                    logger.info("✅ Clearing house info stored successfully.");
    
                    // ✅ Call extra services
                    logger.info("🔄 Running Patient API to fetch patient details...");
                    patientService.testGetPatientData(token, defaultLocation, defaultLocationTimeZone);
    
                    String clearingHouseKey = (String) session.getAttribute("clearing_house_key");
    
                    logger.info("🔄 Generating Combined JSON data...");
                    combinedService.buildCombinedJson(clearingHouseKey);  // <-- pass the key
    
                    logger.info("✅ Login Successful!");
                    return new ModelAndView("redirect:/api/test/");
                } else {
                    logger.error("❌ Login failed! 'result' field is missing in response.");
                    mav.addObject("error", "❌ Login failed! Invalid response from API.");
                }
            } else {
                logger.error("❌ Login failed! Unexpected response status: {}", response.getStatusCode());
                mav.addObject("error", "❌ Login failed! Please check your credentials.");
            }
    
        } catch (HttpClientErrorException e) {
            logger.error("❌ HTTP Error during login: {}", e.getStatusCode());
            logger.error("❌ Response Body: {}", e.getResponseBodyAsString());
            mav.addObject("error", "❌ Login failed! " + e.getStatusCode());
    
        } catch (Exception e) {
            logger.error("❌ Exception during login: {}", e.getMessage(), e);
            mav.addObject("error", "❌ Login failed! Unexpected error occurred.");
        }
    
        mav.setViewName("login");
        return mav;
    }
    
    private void fetchAndStoreClearingHouseInfo(String p_id, HttpSession session) {
        String apiUrl = "https://darwinapi.edvak.com:3000/practice-settings/getPracticeSetting/" + p_id;
    
        try {
            // 🔹 Get token from session
            String token = (String) session.getAttribute("token");
    
            if (token == null || token.isEmpty()) {
                logger.error("❌ No token found in session!");
                return;
            }
    
            HttpHeaders headers = new HttpHeaders();
    
            // ✅ Headers from curl
            headers.setAccept(Arrays.asList(
                MediaType.APPLICATION_JSON,
                MediaType.TEXT_PLAIN,
                MediaType.ALL
            ));
    
            headers.set("Authorization", "Bearer " + token);
            headers.set("p_id", p_id); // ✅ Custom header like in curl
    
            // Optional: if location_id has a value, add it, else skip or clarify
            // headers.set("location_id", "value_if_any");
    
            // Not mandatory but included in curl, so adding for completeness
            headers.set("Origin", "https://darwinapi.edvak.com");
            headers.set("Referer", "https://darwinapi.edvak.com/");
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36 Edg/134.0.0.0");
    
            // For CORS / preflight headers (not usually needed from backend)
            headers.set("Sec-Fetch-Dest", "empty");
            headers.set("Sec-Fetch-Mode", "cors");
            headers.set("Sec-Fetch-Site", "same-site");
            headers.set("sec-ch-ua", "\"Chromium\";v=\"134\", \"Not:A-Brand\";v=\"24\", \"Microsoft Edge\";v=\"134\"");
            headers.set("sec-ch-ua-mobile", "?0");
            headers.set("sec-ch-ua-platform", "\"Windows\"");
    
            HttpEntity<String> entity = new HttpEntity<>(headers);
    
            logger.info("🔹 Calling Practice Settings API: {}", apiUrl);
    
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
    
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Map<String, Object> result = (Map<String, Object>) body.get("result");
    
                if (result != null) {
                    String clearingHouseKey = (String) result.get("clearing_house_key");
                    String clearingHouseName = (String) result.get("clearing_house_name");
    
                    logger.info("✅ clearing_house_key: {}", clearingHouseKey);
                    logger.info("✅ clearing_house_name: {}", clearingHouseName);
    
                    session.setAttribute("clearing_house_key", clearingHouseKey);
                    session.setAttribute("clearing_house_name", clearingHouseName);
                } else {
                    logger.error("❌ No 'result' found in practice settings response.");
                }
            } else {
                logger.error("❌ Failed to fetch practice settings. Status: {}", response.getStatusCode());
            }
    
        } catch (Exception e) {
            logger.error("❌ Exception while fetching practice settings: {}", e.getMessage(), e);
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
