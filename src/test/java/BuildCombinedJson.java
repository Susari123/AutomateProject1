
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class BuildCombinedJson {

    @Test
    public void testBuildCombinedJson() throws IOException {
        // File paths
        String patientDetailsFilePath = "src/test/resources/output/PatientDetails.json";
        String icdCodesFilePath = "src/test/resources/output/ICDCodes.json";
        String cptCodesFilePath = "src/test/resources/output/CPTCodes.json";
        String outputFilePath = "src/test/resources/output/CombinedData.json";

        // Initialize ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse JSON files
        JsonNode patientDetails = objectMapper.readTree(new File(patientDetailsFilePath));
        JsonNode icdCodes = objectMapper.readTree(new File(icdCodesFilePath));
        JsonNode cptCodes = objectMapper.readTree(new File(cptCodesFilePath));

        // Ensure the output file and directories exist
        File outputFile = new File(outputFilePath);
        if (!outputFile.exists()) {
            File parentDir = outputFile.getParentFile();
            if (!parentDir.exists()) {
                boolean dirsCreated = parentDir.mkdirs();
                assertTrue(dirsCreated, "Failed to create directories for: " + outputFilePath);
            }
            boolean fileCreated = outputFile.createNewFile();
            assertTrue(fileCreated, "Failed to create file: " + outputFilePath);
        }

        // Build the combined JSON
        List<JsonNode> combinedData = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < patientDetails.size(); i++) {
            JsonNode patient = patientDetails.get(i);

            // Extract patient details
            String patientName = patient.get("first_name").asText() + " " + patient.get("last_name").asText();
            boolean selfPay = patient.get("selfPay").asBoolean();
            boolean hasInsuranceField = patient.get("hasInsuranceField").asBoolean();

            // Dynamically assign ICD and CPT codes (round-robin)
            String icd = icdCodes.get(i % icdCodes.size()).get("Description").asText();
            String cpt = cptCodes.get(i % cptCodes.size()).get("CPTCode").asText();

            // Determine claimType
            String claimType;
            if (!selfPay && hasInsuranceField) {
                // Set claimType as either Electronic or Paper
                claimType = random.nextBoolean() ? "Electronic" : "Paper";
            } else {
                // Default to Self for selfPay or other cases
                claimType = "Self";
            }

            // Generate random amount (integer or decimal)
            double randomAmount = 100 + (900 * random.nextDouble()); // Random value between 100 and 1000
            String amount;
            if (random.nextBoolean()) {
                // Generate integer amount
                amount = String.valueOf((int) randomAmount);
            } else {
                // Generate decimal amount with 2 decimal places
                BigDecimal formattedAmount = BigDecimal.valueOf(randomAmount).setScale(2, RoundingMode.HALF_UP);
                amount = formattedAmount.toString();
            }

            // Construct the patient record
            JsonNode patientRecord = objectMapper.createObjectNode()
                    .put("patientName", patientName)
                    .put("icd", icd)
                    .put("cpt", cpt)
                    .put("amount", amount)
                    .put("claimType", claimType)
                    .put("Mode", "Generate");

            combinedData.add(patientRecord);
        }

        // Pretty print the combined JSON
        ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(outputFile, combinedData);

        // Validate the file exists and is not empty
        assertTrue(outputFile.exists(), "CombinedData.json file should exist.");
        assertTrue(outputFile.length() > 0, "CombinedData.json file should not be empty.");

        System.out.println("Combined JSON data saved to: " + outputFilePath);
    }
}
