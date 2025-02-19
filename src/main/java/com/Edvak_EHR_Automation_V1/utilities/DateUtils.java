package com.Edvak_EHR_Automation_V1.utilities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

	 public static String[] getCurrentAndPreviousDate() {

	        LocalDate currentDate = LocalDate.now();
	        LocalDate previousDate = currentDate.minusDays(1);
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
	        return new String[] { previousDate.format(formatter), currentDate.format(formatter) };
	    }

	    public static void main(String[] args) {

	        String[] dates = getCurrentAndPreviousDate();
	        System.out.println("Previous Date: " + dates[0]);
	        System.out.println("Current Date: " + dates[1]);
	    }
	}