package com.Edvak_EHR_Automation_V1.utilities;

import java.security.SecureRandom;

public class GenerateRandomNumberBetweenLength {
	public static long generateRandomNumber(int minLength, int maxLength) {
	    if (minLength <= 0 || maxLength <= 0 || minLength > maxLength) {
	        throw new IllegalArgumentException("Lengths must be positive, and minLength should be less than or equal to maxLength");
	    }
	 
	    SecureRandom random = new SecureRandom();
	    // Determine the length randomly between minLength and maxLength
	    int length = minLength + random.nextInt(maxLength - minLength + 1);
	        System.out.println(length);
	    // Generate a number of the desired length
	    // long min = (long) Math.pow(10, length - 1); // Minimum value for the specified length
	    // long max = (long) Math.pow(10, length) - 1; // Maximum value for the specified length

	    return length; // Random number between min and max
	    
	}
}