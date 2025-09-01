package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.List;

public class ChatBotPage {

    WebDriver driver;

    public ChatBotPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//div[normalize-space()='New Chat']")
    private WebElement btnNewChat;

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

    public void sendMessage() {
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
}