package com.Edvak_EHR_Automation_V1.utilities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    // Method to return current date and future date (e.g., 10 days from today)
    public static String[] getCurrentAndFutureDate(int daysInFuture) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        
        // Get the future date by adding days to the current date
        LocalDate futureDate = currentDate.plusDays(daysInFuture);
        
        // Define the desired date format (e.g., "MM/dd/yyyy")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        
        // Format the dates to strings
        String formattedCurrentDate = currentDate.format(formatter);
        String formattedFutureDate = futureDate.format(formatter);
        
        // Return both dates in a String array
        return new String[] { formattedCurrentDate, formattedFutureDate };
    }

    // Test the method
    public static void main(String[] args) {
        String[] dates = getCurrentAndFutureDate(10);
        System.out.println("Current Date: " + dates[0]);
        System.out.println("Future Date (10 days later): " + dates[1]);
    }
}
