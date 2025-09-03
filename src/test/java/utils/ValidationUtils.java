package utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.asserts.SoftAssert;
import pages.ChatBotPage;

import java.util.List;

public class ValidationUtils {

    public static int countKeywordMatches(String response, List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) return 0;
        String lowerResponse = response.toLowerCase();
        int count = 0;
        for (String keyword : keywords) {
            boolean allWordsPresent = true;
            for (String word : keyword.toLowerCase().split(" ")) {
                if (!lowerResponse.contains(word)) {
                    allWordsPresent = false;
                    break;
                }
            }
            if (allWordsPresent) count++;
        }
        return count;
    }

    public static void verifyAccuracy(SoftAssert softAssert, String response, List<String> keywords, int minMatch) {
        int matched = countKeywordMatches(response, keywords);
        softAssert.assertTrue(matched >= minMatch,
                "Accuracy check failed: matched " + matched + "/" + keywords.size() + ". Response: " + response);
    }

    public static void verifyNoHallucination(SoftAssert softAssert, String response, List<String> blacklist, boolean strict, ExtentTest test) {
        if (blacklist == null || blacklist.isEmpty()) return;
        for (String word : blacklist) {
            if (response.toLowerCase().contains(word.toLowerCase())) {
                String msg = "Potential hallucination detected: '" + word + "'";
                if (strict) {
                    softAssert.fail(msg);
                    if (test != null) test.log(Status.FAIL, msg);
                } else {
                    if (test != null) test.log(Status.WARNING, msg);
                }
            }
        }
    }

    public static void verifyConsistency(SoftAssert softAssert, String response, List<String> phrases) {
        if (phrases == null || phrases.isEmpty()) return;
        String lowerResponse = response.toLowerCase();
        for (String phrase : phrases) {
            if (!lowerResponse.contains(phrase.toLowerCase())) {
                softAssert.fail("Consistency check failed: missing phrase '" + phrase + "'. Response: " + response);
            }
        }
    }

    public static void verifyFormatting(SoftAssert softAssert, String response) {
        if (response == null || response.isEmpty()) {
            softAssert.fail("Formatting check failed: empty response.");
            return;
        }
        if (response.contains("<") && !response.contains(">")) {
            softAssert.fail("Formatting issue: broken HTML tags.");
        }
        if (response.trim().endsWith("...")) {
            softAssert.fail("Formatting issue: incomplete response ending with ellipsis.");
        }
    }

    public static void verifyFallback(SoftAssert softAssert, String response, List<String> fallbackMessages) {
        if (fallbackMessages == null || fallbackMessages.isEmpty()) return;
        boolean found = fallbackMessages.stream().anyMatch(msg -> response.toLowerCase().contains(msg.toLowerCase()));
        softAssert.assertTrue(found, "Fallback check failed: no expected fallback message appeared.");
    }

    public static void verifySecurity(SoftAssert softAssert, String response, String input, ExtentTest test) {
        if (response.toLowerCase().contains("<script>")) {
            String msg = "Response contains script for input: " + input;
            softAssert.fail(msg);
            if (test != null) test.log(Status.FAIL, msg);
        }

        List<String> malicious = List.of("ignore instructions", "tell me a joke");
        for (String phrase : malicious) {
            if (response.toLowerCase().contains(phrase)) {
                String msg = "AI acted on malicious prompt: '" + phrase + "'. Input: " + input;
                softAssert.fail(msg);
                if (test != null) test.log(Status.FAIL, msg);
            }
        }
    }

    public static void verifyMultilingualSupport(SoftAssert softAssert, ChatBotPage page, boolean isEnglish, ExtentTest test) {
        if (isEnglish) {
            softAssert.assertTrue(page.isResponseLTR(), "Expected LTR for English.");
            if (test != null) test.log(Status.INFO, "✅ English response validated (LTR).");
        } else {
            softAssert.assertTrue(page.isLastResponseRTL(), "Expected RTL for Arabic.");
            if (test != null) test.log(Status.INFO, "✅ Arabic response validated (RTL).");
        }
    }

    public static void verifyChatWidget(SoftAssert softAssert, ChatBotPage page, ExtentTest test) {
        softAssert.assertTrue(page.isChatWidgetVisible(), "Chat widget not visible.");
        softAssert.assertTrue(page.isInputCleared(), "Input not cleared.");
        page.scrollToBottom();
        softAssert.assertTrue(page.isAutoScrolledToBottom(), "Chat did not auto-scroll.");
        if (test != null) test.log(Status.INFO, "✅ Chat widget validated.");
    }

    public static void verifyAutoScroll(SoftAssert softAssert, ChatBotPage page, int messageCount, ExtentTest test) {
        for (int i = 0; i < messageCount; i++) {
            page.txtSendPrompt("AutoScroll Test " + i);
        }
        softAssert.assertTrue(page.isAutoScrolledToBottom(), "Auto-scroll failed after multiple messages.");
        if (test != null) test.log(Status.INFO, "✅ Verified auto-scroll for " + messageCount + " messages.");
    }

    public static void verifyAccessibility(SoftAssert softAssert, ChatBotPage page, TestCase tc, ExtentTest test) {
        if (tc == null || page == null) return;

        // --- 1. Attribute check (aria-label, placeholder, etc.) ---
        if (tc.getCheckAttribute() != null) {
            String value = null;
            try {
                // Use existing helper for chat input accessibility
                if ("aria-label".equalsIgnoreCase(tc.getCheckAttribute()) ||
                        "data-placeholder".equalsIgnoreCase(tc.getCheckAttribute())) {
                    value = page.getChatInputAccessibleName();
                } else {
                    value = page.getAttributeOfInput(tc.getCheckAttribute());
                }
            } catch (Exception e) {
                value = null;
            }

            if (tc.getExpectedNotNull() != null && tc.getExpectedNotNull()) {
                try {
                    softAssert.assertNotNull(value, "Attribute '" + tc.getCheckAttribute() + "' should not be null.");
                    if (test != null)
                        test.log(Status.PASS, "✅ Attribute '" + tc.getCheckAttribute() + "' is present: " + value);
                } catch (AssertionError ae) {
                    if (test != null) test.log(Status.FAIL, "❌ Attribute '" + tc.getCheckAttribute() + "' is missing.");
                    throw ae;
                }
            } else {
                try {
                    softAssert.assertNull(value, "Attribute '" + tc.getCheckAttribute() + "' should be null.");
                    if (test != null)
                        test.log(Status.PASS, "✅ Attribute '" + tc.getCheckAttribute() + "' correctly absent.");
                } catch (AssertionError ae) {
                    if (test != null)
                        test.log(Status.FAIL, "❌ Attribute '" + tc.getCheckAttribute() + "' should be null but was: " + value);
                    throw ae;
                }
            }
        }

        // --- 2. Keyboard navigation check ---
        if (tc.getCheckKeyboardNavigation() != null && tc.getCheckKeyboardNavigation()) {
            String focusedId = null;
            try {
                WebElement active = page.getFocusedElement();
                active.sendKeys(Keys.TAB);
                focusedId = page.getFocusedElement().getAttribute("id");

                softAssert.assertEquals(focusedId, tc.getExpectedFocusedId(),
                        "Expected focus on element with id: " + tc.getExpectedFocusedId());

                if (test != null) test.log(Status.PASS, "✅ Keyboard navigation focused on: " + focusedId);

            } catch (AssertionError ae) {
                if (test != null) test.log(Status.FAIL, "❌ Keyboard focus mismatch. Expected: "
                        + tc.getExpectedFocusedId() + ", Actual: " + focusedId);
                throw ae;
            } catch (Exception e) {
                if (test != null) test.log(Status.FAIL, "❌ Error during keyboard navigation check: " + e.getMessage());
            }
        }

        // --- 3. Role attribute check ---
        if (tc.getCheckRoleAttribute() != null && tc.getCheckRoleAttribute()) {
            String role = null;
            try {
                role = page.getRoleOfMessages();

                softAssert.assertEquals(role, tc.getExpectedRole(),
                        "Expected role attribute: " + tc.getExpectedRole());

                if (test != null) test.log(Status.PASS, "✅ Role attribute validated: " + role);

            } catch (AssertionError ae) {
                if (test != null) test.log(Status.FAIL, "❌ Role attribute mismatch. Expected: "
                        + tc.getExpectedRole() + ", Actual: " + role);
                throw ae;
            } catch (Exception e) {
                if (test != null) test.log(Status.FAIL, "❌ Error during role attribute check: " + e.getMessage());
            }
        }
    }
}