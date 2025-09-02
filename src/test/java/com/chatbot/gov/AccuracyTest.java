package com.chatbot.gov;

import org.testng.annotations.BeforeMethod;
import utils.JsonDataReader;
import com.aventstack.extentreports.Status;
import common.TestBase;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.ChatBotPage;
import utils.TestCase;
import utils.ValidationUtils;

import java.time.Duration;

public class AccuracyTest extends TestBase {

    ChatBotPage chatBotPage;

    @BeforeMethod
    public void setUpPage() {
        chatBotPage = new ChatBotPage(driver);
    }

    @Test(dataProvider = "accuracyData")
    public void tc01_accuracyValidation(TestCase tc) {

        test = extent.createTest("Accuracy Validation : " + tc.getId());

        chatBotPage.enterBotRequest(tc.getQuestion());
        chatBotPage.btnSendPrompt();

        String response = chatBotPage.BotResponse(Duration.ofSeconds(25), Duration.ofMillis(300));
        int matchCount = ValidationUtils.countKeywordMatches(response, tc.getExpected_keywords());

        test.log(Status.INFO, "Bot Response: " + response);
        test.log(Status.INFO, "Expected Keywords: " + tc.getExpected_keywords());
        test.log(Status.INFO, "Matched Count: " + matchCount);

        Assert.assertTrue(matchCount >= tc.getMin_keyword_match(), "Accuracy check failed for: " + tc.getQuestion());
    }

    @DataProvider(name = "accuracyData")
    public Object[][] provideAccuracyData() {
        return JsonDataReader.getTestData("testData/accuracy.json", TestCase.class);
    }
}
