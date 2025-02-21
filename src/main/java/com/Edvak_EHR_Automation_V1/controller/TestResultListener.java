package com.Edvak_EHR_Automation_V1.controller;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;

public class TestResultListener implements IResultListener {

    private boolean hasFailures = false;

    @Override
    public void onTestFailure(ITestResult result) {
        hasFailures = true;
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        hasFailures = true;
    }

    @Override
    public void onFinish(ITestContext context) {
        if (context.getFailedTests().size() > 0) {
            hasFailures = true;
        }
    }

    public boolean hasFailures() {
        return hasFailures;
    }
}
