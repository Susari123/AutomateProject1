package com.Edvak_EHR_Automation_V1.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DataFetchService {

    private final String BASE_URL = "https://darwinapi.edvak.com:3000";
    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, Object> practiceDataCache = new HashMap<>();

    public Map<String, Object> fetchAndStoreData(String p_id) {
        String[] endpoints = {
                "/billing/get-all-coding-billing?p_id=" + p_id,
                "/patients/get-all?p_id=" + p_id,
                "/claims/get-all?p_id=" + p_id,
                "/appointments/get-all?p_id=" + p_id,
                "/medications/get-all?p_id=" + p_id
        };

        Map<String, Object> collectedData = new HashMap<>();
        for (String endpoint : endpoints) {
            String apiUrl = BASE_URL + endpoint;
            try {
                Map response = restTemplate.getForObject(apiUrl, Map.class);
                if (response != null) {
                    collectedData.put(endpoint, response);
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error fetching data from " + apiUrl + ": " + e.getMessage());
            }
        }

        // Store in cache for future retrieval
        practiceDataCache.put(p_id, collectedData);
        return collectedData;
    }

    @SuppressWarnings("unchecked")
public Map<String, Object> getPracticeData(String p_id) {
    return (Map<String, Object>) practiceDataCache.getOrDefault(p_id, new HashMap<>());
}

    
}
