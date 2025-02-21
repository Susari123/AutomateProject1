package com.Edvak_EHR_Automation_V1.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OutputController {

    private final TestRunnerController testRunnerController;

    public OutputController(TestRunnerController testRunnerController) {
        this.testRunnerController = testRunnerController;
    }

    @GetMapping("/viewOutput")
    public ModelAndView viewOutput(HttpSession session) {
        // Redirect to login if the user is not authenticated
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelAndView mav = new ModelAndView("output");

        // Get the last test output from TestRunnerController
        String lastTestOutput = testRunnerController.getLastTestOutput();

        if (lastTestOutput == null || lastTestOutput.isBlank()) {
            mav.addObject("consoleOutput", null);
        } else {
            mav.addObject("consoleOutput", lastTestOutput);
            mav.addObject("consoleOutputTrimmed", lastTestOutput.contains("...[Output trimmed]"));
        }

        return mav;
    }
}
