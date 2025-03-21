package com.Edvak_EHR_Automation_V1.service;

public class SessionData {
    private static String userEmail;
    private static String userPassword;

    // ✅ Store login credentials
    public static void setUserCredentials(String email, String password) {
        userEmail = email;
        userPassword = password;
    }

    // ✅ Retrieve email
    public static String getUserEmail() {
        return userEmail;
    }

    // ✅ Retrieve password
    public static String getUserPassword() {
        return userPassword;
    }
}
