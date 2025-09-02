package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.List;

public class ChatBotPage {

    WebDriver driver;
    JavascriptExecutor js;

    public ChatBotPage(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//div[normalize-space()='New Chat']")
    private WebElement btnNewChat;

    @FindBy(id = "chat-input-container")
    private WebElement chatWindow;

    @FindBy(id = "chat-input")
    private WebElement txtChatInput;

    @FindBy(id = "send-message-button")
    private WebElement btnSendMessage;

    @FindBy(xpath = "(//div[contains(@class, 'w-full text-[16px] text-typography-titles leading-[24px] flex flex-col relative')])[last()]")
    private List<WebElement> chatBotResponses;

    @FindBy(xpath = "//button[contains(@class, 'rounded-full') and contains(@class, 'p-1.5')]")
    private List<WebElement> roundedButtons;

    public void startNewChat() {
        btnNewChat.click();
    }

    public void enterBotRequest(String text) {
        txtChatInput.sendKeys(text);
    }

    public void btnSendPrompt() {
        btnSendMessage.click();
    }

    public String BotResponse(Duration timeout, Duration polling) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver).withTimeout(timeout).pollingEvery(polling).ignoring(Exception.class);

        return wait.until(driver -> {
            List<WebElement> responses = chatBotResponses;
            if (!responses.isEmpty()) {
                WebElement lastResponse = responses.get(responses.size() - 1);
                String lastText = lastResponse.getText().trim();

                List<String> placeholders = List.of("Just a sec", "Thinking through", "Formulating");
                if (lastText.isEmpty() || placeholders.stream().anyMatch(lastText::contains)) {
                    return null;
                }

                long startTime = System.currentTimeMillis();
                String previousText = "";
                boolean isSpinnerVisible = !roundedButtons.isEmpty() && roundedButtons.get(roundedButtons.size() - 1).isDisplayed();
                while (!lastText.equals(previousText) || isSpinnerVisible) {
                    previousText = lastText;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                    lastText = lastResponse.getText().trim();

                    isSpinnerVisible = !roundedButtons.isEmpty() &&
                            roundedButtons.get(roundedButtons.size() - 1).isDisplayed();
                    if (System.currentTimeMillis() - startTime > timeout.toMillis()) {
                        break;
                    }
                }
                return lastText; // real bot answer
            }
            return null;
        });
    }

    // Actions
    public void txtSendPrompt(String text) {
        txtChatInput.sendKeys(text);
        txtChatInput.sendKeys(Keys.ENTER); // accessibility check (Enter key)
    }

    public boolean isMessageDisplayed(String expected) {
        WebElement lastMessage = driver.findElement(By.cssSelector(".chat-message:last-child"));
        return lastMessage.getText().contains(expected);
    }

    private long getJsValueAsLong(String script, WebElement element) {
        Object result = js.executeScript(script, element);
        return (result != null) ? ((Number) result).longValue() : 0L;
    }

    // Scroll
    public long getScrollHeight() {
        return getJsValueAsLong("return arguments[0].scrollHeight;", chatWindow);
    }

    public long getClientHeight() {
        return getJsValueAsLong("return arguments[0].clientHeight;", chatWindow);
    }

    public long getScrollTop() {
        return getJsValueAsLong("return arguments[0].scrollTop;", chatWindow);
    }

    public void scrollToTop() {
        js.executeScript("arguments[0].scrollTop = 0;", chatWindow);
    }

    public void scrollToBottom() {
        js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", chatWindow);
    }

    public boolean isAutoScrolledToBottom() {
        long scrollHeight = getScrollHeight();
        long clientHeight = getClientHeight();
        long scrollTop = getScrollTop();
        return scrollTop >= (scrollHeight - clientHeight - 5);
    }

    // Accessibility
    public String getChatInputAccessibleName() {
        // Try aria-label first
        String ariaLabel = txtChatInput.getAttribute("aria-label");
        if (ariaLabel != null && !ariaLabel.isEmpty()) return ariaLabel;
        // Try placeholder-like attributes inside ProseMirror
        try {
            WebElement placeholderElement = txtChatInput.findElement(By.cssSelector("p[data-placeholder]"));
            String placeholder = placeholderElement.getAttribute("data-placeholder");
            if (placeholder != null && !placeholder.isEmpty()) return placeholder;
        } catch (NoSuchElementException e) {
            // ignore if not found
        }
        // Fallback
        return null;
    }

    public WebElement getFocusedElement() {
        return driver.switchTo().activeElement();
    }

    public boolean isLastResponseLTR() {
        String dir = chatBotResponses.get(chatBotResponses.size() - 1).getCssValue("direction");
        return dir.equalsIgnoreCase("ltr");
    }

    public boolean isLastResponseRTL() {
        WebElement lastResponse = chatBotResponses.get(chatBotResponses.size() - 1);
        return isElementOrAncestorRTL(lastResponse);
    }

    private boolean isElementOrAncestorRTL(WebElement element) {
        // Check this element's CSS direction
        String dir = element.getCssValue("direction");
        if ("rtl".equalsIgnoreCase(dir)) {
            return true;
        }

        //  Check if element text contains Arabic characters
        String text = element.getText();
        boolean containsArabic = text.codePoints().anyMatch(
                c -> (c >= 0x0600 && c <= 0x06FF) || (c >= 0x0750 && c <= 0x077F)
        );
        if (containsArabic) {
            return true;
        }

        //  Recurse to parent if not yet at the root
        try {
            WebElement parent = element.findElement(By.xpath(".."));
            // Stop recursion if parent is the body element
            if ("html".equalsIgnoreCase(parent.getTagName()) || parent == element) {
                return false;
            }
            return isElementOrAncestorRTL(parent);
        } catch (Exception e) {
            // If no parent or any other error, assume not RTL
            return false;
        }
    }
}