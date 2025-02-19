package com.Edvak_EHR_Automation_V1.utilities;

import java.io.IOException;

public class GenerateRandomNumberOfLengthN {

	public long generateRandomNumber(int n) throws IOException {
		long number = (long) (Math.floor(Math.random() * (9 * Math.pow(10, n - 1))) + Math.pow(10, (n - 1)));
		return number;

	}
}
