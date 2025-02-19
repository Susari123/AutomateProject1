package com.Edvak_EHR_Automation_V1.utilities;

public class GenerateRandomEmailAddress {

	public String generateRandomEmailAddress() {
		String emailAddress = "";
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		while (emailAddress.length() < 5) {
			int character = (int) (Math.random() * 26);
			emailAddress += alphabet.substring(character, character + 1);
			emailAddress += Integer.valueOf((int) (Math.random() * 99)).toString();
			emailAddress += "@" + "gmail.com";

		}
		return emailAddress;
	}
}