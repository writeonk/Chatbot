package com.test.app;

import com.aventstack.extentreports.Status;
import common.TestBase;
import org.openqa.selenium.Dimension;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;

public class LoginTest extends TestBase {

    @Test(groups = "Smoke")
    public void tc00VerifyAppURL() {
        test = extent.createTest("Verify App URL", "Open Chatbot link").assignCategory("Functional_TestCase");
        logger.info("Verify Chatbot Application URL");

        String url = properties.getProperty("app.url");
        webdriver.openURL(url);
        test.log(Status.INFO, "Verify URL");
        logger.info("Verify URL");
    }

    @Test(groups = "Smoke")
    public void tc01VerifyAppLogin() {
        test = extent.createTest("Verify App Login", "Login with Valid Credentials").assignCategory("Functional_TestCase");
        logger.info("Verify Login");

        String email = properties.getProperty("app.email");
        String password = properties.getProperty("app.password");

        LoginPage loginPage = new LoginPage(driver);

        loginPage.clickLoginUsingCreds();
        test.log(Status.INFO, "Clicked on Login Using Credentials");
        logger.info("Click on Login Using Credentials button");

        loginPage.enterEmail(email);
        test.log(Status.INFO, "Entered email");
        logger.info("Enter email");

        loginPage.enterPassword(password);
        test.log(Status.INFO, "Entered password");
        logger.info("Enter Password");

        loginPage.clickSignIn();
        test.log(Status.INFO, "Clicked on SignIn");
        logger.info("Click on Login");
    }

    @Test
    public void tc02ChatWidgetLoadsOnDesktopAndMobile() {

        test = extent.createTest("Verify Chat Widget Loads On Desktop And Mobile", "").assignCategory("Functional_TestCase");
        logger.info("Verify Chat Widget Loads On Desktop And Mobile");

        Dimension originalSize = driver.manage().window().getSize();

        try {
            // Desktop View
            driver.manage().window().setSize(new Dimension(1920, 1080));
            if (LoginPage.isChatWidgetDisplayed()) {
                test.log(Status.PASS, "Chat widget is visible in Desktop view! 1920 : 1080");
                logger.info("Chat widget is visible in Desktop view! 1920 : 1080");
            } else {
                test.log(Status.FAIL, "Chat widget is NOT visible in Desktop view! 1920 : 1080");
                logger.error("Chat widget is NOT visible in Desktop view! 1920 : 1080");
                Assert.fail("Chat widget not visible in Desktop view! 1920 : 1080"); // fail the test
            }

            // Mobile View
            driver.manage().window().setSize(new Dimension(375, 812)); // iPhone X size
            if (LoginPage.isChatWidgetDisplayed()) {
                test.log(Status.PASS, "Chat widget is visible in Mobile view! 375 : 812");
                logger.info("Chat widget is visible in Mobile view! 375 : 812");
            } else {
                test.log(Status.FAIL, "Chat widget is NOT visible in Mobile view! 375 : 812");
                logger.error("Chat widget is NOT visible in Mobile view! 375 : 812");
                Assert.fail("Chat widget not visible in Mobile view! 375 : 812"); // fail the test
            }

        } finally {
            // Reset to original size
            driver.manage().window().setSize(originalSize);
        }
    }

}
