package com.chatbot.gov;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import common.TestBase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.ChatBotPage;
import utils.JsonDataReader;
import utils.TestCase;
import utils.TestType;
import utils.ValidationUtils;

import java.time.Duration;

public class ChatBotGenericTest extends TestBase {

    private ChatBotPage chatBotPage;

    @BeforeMethod
    public void setupPage() {
        chatBotPage = new ChatBotPage(driver);
    }

    public void runTestCase(TestCase tc, TestType type) {

        test = extent.createTest("Chatbot Test: " + tc.getId() + " [" + type + "]")
                .assignCategory("Functional_TestCase");

        String question = tc.getQuestion();
        chatBotPage.enterBotRequest(question);
        chatBotPage.btnSendPrompt();
        String response = chatBotPage.BotResponse(Duration.ofSeconds(25), Duration.ofMillis(300));

        SoftAssert softAssert = new SoftAssert();

        switch (type) {
            case ACCURACY:
                ValidationUtils.verifyAccuracy(softAssert, response, tc.getExpected_keywords(), tc.getMin_keyword_match());
                break;
            case HALLUCINATION:
                ValidationUtils.verifyNoHallucination(softAssert, response, tc.getBlacklist(), tc.isStrictHallucination(), test);
                break;
            case CONSISTENCY:
                ValidationUtils.assertConsistency(softAssert, response, tc.getConsistencyPhrases());
                break;
            case FORMATTING:
                ValidationUtils.assertFormatting(softAssert, response);
                break;
            case FALLBACK:
                ValidationUtils.assertFallback(softAssert, response, tc.getFallbackMessages());
                break;
            case SECURITY:
                ValidationUtils.assertSecurity(softAssert, response, tc.getQuestion(), test);
                break;
        }

        test.log(Status.INFO, MarkupHelper.createLabel("Question: " + question, ExtentColor.BLUE));
        // Assert all SoftAssert failures WITHOUT stopping next tests
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            test.log(Status.FAIL, MarkupHelper.createLabel("SoftAssert failures: " + e.getMessage(), ExtentColor.RED));
            logger.error("SoftAssert failures: {}", e.getMessage());
            // do NOT rethrow
        }

        test.log(Status.INFO, MarkupHelper.createLabel("Bot Response: " + response, ExtentColor.YELLOW));
    }

    @Test(dataProvider = "accuracyData")
    public void tc01_accuracyValidation(TestCase tc) {
        runTestCase(tc, TestType.ACCURACY);
    }

    @DataProvider(name = "accuracyData")
    public Object[][] provideAccuracyData() {
        return JsonDataReader.getTestData("testData/accuracy.json", TestCase.class);
    }

    @Test(dataProvider = "hallucinationData")
    public void tc02_hallucinationValidation(TestCase tc) {
        runTestCase(tc, TestType.HALLUCINATION);
    }

    @DataProvider(name = "hallucinationData")
    public Object[][] provideHallucinationData() {
        return JsonDataReader.getTestData("testData/hallucination.json", TestCase.class);
    }

    @Test(dataProvider = "consistencyData")
    public void tc03_consistencyValidation(TestCase tc) {
        runTestCase(tc, TestType.CONSISTENCY);
    }

    @DataProvider(name = "consistencyData")
    public Object[][] provideConsistencyData() {
        return JsonDataReader.getTestData("testData/consistency.json", TestCase.class);
    }

    @Test(dataProvider = "formattingData")
    public void tc04_formattingValidation(TestCase tc) {
        runTestCase(tc, TestType.FORMATTING);
    }

    @DataProvider(name = "formattingData")
    public Object[][] provideFormattingData() {
        return JsonDataReader.getTestData("testData/formatting.json", TestCase.class);
    }

    @Test(dataProvider = "fallbackData")
    public void tc05_fallbackValidation(TestCase tc) {
        runTestCase(tc, TestType.FALLBACK);
    }

    @DataProvider(name = "fallbackData")
    public Object[][] provideFallbackData() {
        return JsonDataReader.getTestData("testData/fallback.json", TestCase.class);
    }

    @Test(dataProvider = "securityData")
    public void tc06_securityValidation(TestCase tc) {
        runTestCase(tc, TestType.SECURITY);
    }

    @DataProvider(name = "securityData")
    public Object[][] provideSecurityData() {
        return JsonDataReader.getTestData("testData/security.json", TestCase.class);
    }
}