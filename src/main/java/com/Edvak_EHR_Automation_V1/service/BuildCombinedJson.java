package com.Edvak_EHR_Automation_V1.service;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Service
public class BuildCombinedJson {

    public  void buildCombinedJson() throws IOException {
        String patientDetailsFilePath = "src/test/resources/output/PatientDetails.json";
        String icdCodesFilePath = "src/test/resources/output/ICDCodes.json";
        String cptCodesFilePath = "src/test/resources/output/CPTCodes.json";
        String outputFilePath = "src/test/resources/output/CombinedData.json";

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode patientDetails = objectMapper.readTree(new File(patientDetailsFilePath));
        JsonNode icdCodes = objectMapper.readTree(new File(icdCodesFilePath));
        JsonNode cptCodes = objectMapper.readTree(new File(cptCodesFilePath));

        List<JsonNode> combinedData = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < patientDetails.size(); i++) {
            JsonNode patient = patientDetails.get(i);

            String patientName = patient.get("first_name").asText() + " " + patient.get("last_name").asText();
            boolean selfPay = patient.get("selfPay").asBoolean();
            boolean hasInsuranceField = patient.get("hasInsuranceField").asBoolean();

            String icd = icdCodes.get(i % icdCodes.size()).get("Description").asText();
            String cpt = cptCodes.get(i % cptCodes.size()).get("CPTCode").asText();

            String claimType = (!selfPay && hasInsuranceField) ? 
                (random.nextBoolean() ? "Electronic" : "Paper") : "Self";

            double randomAmount = 100 + (900 * random.nextDouble());
            String amount = random.nextBoolean() ? 
                String.valueOf((int) randomAmount) :
                BigDecimal.valueOf(randomAmount).setScale(2, RoundingMode.HALF_UP).toString();

            JsonNode patientRecord = objectMapper.createObjectNode()
                    .put("patientName", patientName)
                    .put("icd", icd)
                    .put("cpt", cpt)
                    .put("amount", amount)
                    .put("claimType", claimType)
                    .put("Mode", "Generate");

            combinedData.add(patientRecord);
        }

        ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(new File(outputFilePath), combinedData);

        System.out.println("✅ Combined JSON saved to: " + outputFilePath);
    }
}
