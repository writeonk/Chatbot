package com.test.app;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import common.TestBase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.ChatBotPage;
import utils.TestCase;
import utils.TestData;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Duration;
import java.util.List;

public class ChatBotTest extends TestBase {

    ChatBotPage chatBotPage;

    @BeforeMethod
    public void setUpPage() {
        // driver is already initialized in TestBase
        chatBotPage = new ChatBotPage(driver);
    }

    @DataProvider(name = "chatBotTestData")
    public Object[][] getChatBotTestData() throws FileNotFoundException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader("src/test/resources/testData.json"));
        TestData testData = gson.fromJson(reader, TestData.class);

        Object[][] data = new Object[testData.getTests().size()][1];
        for (int i = 0; i < testData.getTests().size(); i++) {
            data[i][0] = testData.getTests().get(i);
        }
        return data;
    }

    private int countKeywordMatches(String response, List<String> keywords) {
        if (response == null || response.isEmpty()) return 0;
        String normalizedResponse = response.replaceAll("[\\n\\r]+", " ")
                .replaceAll("[^a-zA-Z0-9 ]", "")
                .toLowerCase();
        int count = 0;
        for (String keyword : keywords) {
            String normalizedKeyword = keyword.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
            if (normalizedResponse.contains(normalizedKeyword)) {
                count++;
            }
        }
        return count;
    }

    @Test
    public void tc01testEnglishMessage() {

        test = extent.createTest("Verify Chatbot English Response", "").assignCategory("Functional_TestCase");
        logger.info("Verify Chatbot English Response");

        String english_message = "Hello";

        // chatBotPage.startNewChat();
        chatBotPage.enterBotRequest(english_message);
        chatBotPage.sendMessage();

        String response = chatBotPage.BotResponse(Duration.ofSeconds(5), Duration.ofMillis(300));
        System.out.println("Chatbot response: " + response);
        test.log(Status.INFO, "Bot Response: " + response);

        Assert.assertTrue(chatBotPage.isLastResponseLTR(), "Response should be LTR for English");
    }

    @Test
    public void tc02testArabicMessage() {

        test = extent.createTest("Verify Chatbot Arabic Response", "").assignCategory("Functional_TestCase");
        logger.info("Verify Chatbot Arabic Response");

        String arabic_message = "مرحبًا";
        chatBotPage.enterBotRequest(arabic_message);
        chatBotPage.sendMessage();

        String response = chatBotPage.BotResponse(Duration.ofSeconds(5), Duration.ofMillis(300));
        System.out.println("Chatbot response: " + response);
        test.log(Status.INFO, "Bot Response: " + response);
        Assert.assertTrue(chatBotPage.isLastResponseRTL(), "Response should be RTL for Arabic");
    }

    @Test(dataProvider = "chatBotTestData", enabled = false)
    public void chatBotValidation(TestCase tc) {

        test = extent.createTest("Verify Chatbot Response Accuracy", "").assignCategory("Functional_TestCase");
        logger.info("Verify Chatbot Response Accuracy");

        String question = tc.getQuestion();
        List<String> expectedKeywords = tc.getExpected_keywords();
        int minMatch = tc.getMin_keyword_match();
        logger.info("Executing Test for Question: {}", question);

        // chatBotPage.startNewChat();
        chatBotPage.enterBotRequest(question);
        chatBotPage.sendMessage();

        String actualResponse = chatBotPage.BotResponse(Duration.ofSeconds(30), Duration.ofMillis(300));
        test.log(Status.INFO, "Bot Response: " + actualResponse);

        int matchCount = 0;

        // Validate response
        matchCount = countKeywordMatches(actualResponse, expectedKeywords);

        logger.info("Expected Keywords: {}", expectedKeywords);
        test.log(Status.INFO, MarkupHelper.createLabel("Matched Keywords: " + matchCount + "/" + expectedKeywords.size(), ExtentColor.GREEN));

        logger.info("Matched Keywords: {}/{}", matchCount, expectedKeywords.size());
        test.log(Status.INFO, MarkupHelper.createLabel("Expected Keywords: " + expectedKeywords, ExtentColor.BLUE));

        logger.info("Q: {} | Bot: {} | Keywords Matched: {}/{}",
                question, actualResponse, matchCount, expectedKeywords.size());
        test.log(Status.PASS,
                String.format("Q: %s | Bot: %s | Keywords Matched: %d/%d",
                        question, actualResponse, matchCount, expectedKeywords.size()));

        Assert.assertTrue(
                matchCount >= minMatch,
                "\n❌ Response validation failed" +
                        "\nQuestion: " + question +
                        "\nExpected Keywords: " + expectedKeywords +
                        "\nActual Response: " + actualResponse +
                        "\nMatched: " + matchCount + "/" + expectedKeywords.size()
        );

        System.out.println("\n✅ Test Passed");
        System.out.println("Question: " + question);
        System.out.println("Bot Response: " + actualResponse);
        System.out.println("Keywords Matched: " + matchCount + "/" + expectedKeywords.size());
    }

}