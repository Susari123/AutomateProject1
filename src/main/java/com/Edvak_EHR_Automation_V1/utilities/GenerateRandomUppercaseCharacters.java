package com.Edvak_EHR_Automation_V1.utilities;

public class GenerateRandomUppercaseCharacters {
	public String generateStringUppercaseCharacters(int length) {

		char[] ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

		StringBuilder random = new StringBuilder();

		for (int i = 0; i < length; i++) {
			int index = (int) (Math.random() * ALPHANUMERIC.length);
			random.append(ALPHANUMERIC[index]);
		}
		return random.toString();
	}

}
