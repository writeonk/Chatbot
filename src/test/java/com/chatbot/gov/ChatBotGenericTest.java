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

        SoftAssert softAssert = new SoftAssert();
        String response = null;

        if (type != TestType.ACCESSIBILITY) {
            if (tc.getQuestion() != null && !tc.getQuestion().isEmpty()) {
                chatBotPage.enterBotRequest(tc.getQuestion());
                chatBotPage.btnSendPrompt();
                response = chatBotPage.BotResponse(Duration.ofSeconds(25), Duration.ofMillis(300));
            }
        }

        switch (type) {
            case ACCURACY:
                ValidationUtils.verifyAccuracy(softAssert, response, tc.getExpected_keywords(), tc.getMin_keyword_match());
                break;

            case HALLUCINATION:
                ValidationUtils.verifyNoHallucination(softAssert, response, tc.getBlacklist(), tc.isStrictHallucination(), test);
                break;

            case CONSISTENCY:
                ValidationUtils.verifyConsistency(softAssert, response, tc.getConsistencyPhrases());
                break;

            case FORMATTING:
                ValidationUtils.verifyFormatting(softAssert, response);
                break;

            case FALLBACK:
                ValidationUtils.verifyFallback(softAssert, response, tc.getFallbackMessages());
                break;

            case SECURITY:
                ValidationUtils.verifySecurity(softAssert, response, tc.getQuestion(), test);
                break;

            case UI_BEHAVIOR:
                ValidationUtils.verifyMultilingualSupport(
                        softAssert,
                        chatBotPage,
                        tc.isEnglish(),
                        test
                );

                ValidationUtils.verifyChatWidget(
                        softAssert,
                        chatBotPage,
                        test
                );

                if (tc.getMessageCount() > 0) {
                    ValidationUtils.verifyAutoScroll(
                            softAssert,
                            chatBotPage,
                            tc.getMessageCount(),
                            test
                    );
                }
                break;

            case ACCESSIBILITY:
                ValidationUtils.verifyAccessibility(softAssert, chatBotPage, tc, test);
                break;
        }

        if (tc.getQuestion() != null) {
            test.log(Status.INFO, MarkupHelper.createLabel("Question: " + tc.getQuestion(), ExtentColor.BLUE));
            test.log(Status.INFO, MarkupHelper.createLabel("Bot Response: " + response, ExtentColor.GREEN));
            logger.info("Question: {}", tc.getQuestion());
            logger.info("Bot Response: {}", response);
        }

        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            test.log(Status.FAIL, MarkupHelper.createLabel("SoftAssert failures: " + e.getMessage(), ExtentColor.RED));
            logger.info("SoftAssert failures: {}", e.getMessage());
        }
    }

    @Test(dataProvider = "accuracyData")
    public void tc01_accuracy(TestCase tc) {
        runTestCase(tc, TestType.ACCURACY);
    }

    @DataProvider(name = "accuracyData")
    public Object[][] provideAccuracyData() {
        return JsonDataReader.getTestData("testData/accuracy.json", TestCase.class);
    }

    @Test(dataProvider = "hallucinationData")
    public void tc02_hallucination(TestCase tc) {
        runTestCase(tc, TestType.HALLUCINATION);
    }

    @DataProvider(name = "hallucinationData")
    public Object[][] provideHallucinationData() {
        return JsonDataReader.getTestData("testData/hallucination.json", TestCase.class);
    }

    @Test(dataProvider = "consistencyData")
    public void tc03_consistency(TestCase tc) {
        runTestCase(tc, TestType.CONSISTENCY);
    }

    @DataProvider(name = "consistencyData")
    public Object[][] provideConsistencyData() {
        return JsonDataReader.getTestData("testData/consistency.json", TestCase.class);
    }

    @Test(dataProvider = "formattingData")
    public void tc04_formatting(TestCase tc) {
        runTestCase(tc, TestType.FORMATTING);
    }

    @DataProvider(name = "formattingData")
    public Object[][] provideFormattingData() {
        return JsonDataReader.getTestData("testData/formatting.json", TestCase.class);
    }

    @Test(dataProvider = "fallbackData")
    public void tc05_fallback(TestCase tc) {
        runTestCase(tc, TestType.FALLBACK);
    }

    @DataProvider(name = "fallbackData")
    public Object[][] provideFallbackData() {
        return JsonDataReader.getTestData("testData/fallback.json", TestCase.class);
    }

    @Test(dataProvider = "securityData")
    public void tc06_security(TestCase tc) {
        runTestCase(tc, TestType.SECURITY);
    }

    @DataProvider(name = "securityData")
    public Object[][] provideSecurityData() {
        return JsonDataReader.getTestData("testData/security.json", TestCase.class);
    }

    @Test(dataProvider = "uiBehaviorData")
    public void tc07_uiBehavior(TestCase tc) {
        runTestCase(tc, TestType.UI_BEHAVIOR);
    }

    @DataProvider(name = "uiBehaviorData")
    public Object[][] provideUIBehaviorData() {
        return JsonDataReader.getTestData("testData/UI.json", TestCase.class);
    }

    @Test(dataProvider = "accessibilityData")
    public void tc08_accessibility(TestCase tc) {
        runTestCase(tc, TestType.ACCESSIBILITY);
    }

    @DataProvider(name = "accessibilityData")
    public Object[][] provideAccessibilityData() {
        return JsonDataReader.getTestData("testData/accessibility.json", TestCase.class);
    }
}