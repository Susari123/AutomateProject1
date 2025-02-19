package com.Edvak_EHR_Automation_V1.utilities;

public class GenerateRandomSmallCharacters {

	public String generateStringSmallCharacters(int length) {

		char[] ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyz".toCharArray();

		StringBuilder random = new StringBuilder();

		for (int i = 0; i < length; i++) {
			int index = (int) (Math.random() * ALPHANUMERIC.length);
			random.append(ALPHANUMERIC[index]);
		}
		return random.toString();
	}

}
