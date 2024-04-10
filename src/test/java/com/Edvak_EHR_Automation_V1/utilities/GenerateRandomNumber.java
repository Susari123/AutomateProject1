package com.Edvak_EHR_Automation_V1.utilities;

public class GenerateRandomNumber {
	public long generateRandomNumber() throws InterruptedException {

		long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
		return number;
	}

}
