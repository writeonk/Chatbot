package utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TestCase {

    private String id;
    private String question;

    // Functional / Accuracy
    private List<String> expected_keywords;
    private int min_keyword_match;

    // Hallucination / Security
    private List<String> blacklist;
    private boolean strictHallucination;

    // Consistency
    private List<String> consistencyPhrases;

    // Fallback
    private List<String> fallbackMessages;

    // UI Behavior
    private Boolean expectedLTR;
    private Boolean expectedInputCleared;

    @JsonProperty("isEnglish")
    private Boolean english;

    private Integer messageCount;

    // Accessibility-specific fields
    private String description;
    private String checkAttribute;
    private Boolean expectedNotNull;
    private Boolean checkKeyboardNavigation;
    private String expectedFocusedId;
    private Boolean checkRoleAttribute;
    private String expectedRole;

    private String testType;

    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getExpected_keywords() {
        return expected_keywords;
    }

    public int getMin_keyword_match() {
        return min_keyword_match;
    }

    public List<String> getBlacklist() {
        return blacklist;
    }

    public boolean isStrictHallucination() {
        return strictHallucination;
    }

    public List<String> getConsistencyPhrases() {
        return consistencyPhrases;
    }

    public List<String> getFallbackMessages() {
        return fallbackMessages;
    }

    public Integer getMessageCount() {
        return messageCount;
    }

    public Boolean getExpectedLTR() {
        return expectedLTR;
    }

    public void setExpectedLTR(Boolean expectedLTR) {
        this.expectedLTR = expectedLTR;
    }

    public Boolean getExpectedInputCleared() {
        return expectedInputCleared;
    }

    public void setExpectedInputCleared(Boolean expectedInputCleared) {
        this.expectedInputCleared = expectedInputCleared;
    }

    public Boolean isEnglish() {
        return english;
    }

    public void setEnglish(Boolean english) {
        this.english = english;
    }

    public String getCheckAttribute() {
        return checkAttribute;
    }

    public void setCheckAttribute(String checkAttribute) {
        this.checkAttribute = checkAttribute;
    }

    public Boolean getExpectedNotNull() {
        return expectedNotNull;
    }

    public void setExpectedNotNull(Boolean expectedNotNull) {
        this.expectedNotNull = expectedNotNull;
    }

    public Boolean getCheckKeyboardNavigation() {
        return checkKeyboardNavigation;
    }

    public void setCheckKeyboardNavigation(Boolean checkKeyboardNavigation) {
        this.checkKeyboardNavigation = checkKeyboardNavigation;
    }

    public String getExpectedFocusedId() {
        return expectedFocusedId;
    }

    public void setExpectedFocusedId(String expectedFocusedId) {
        this.expectedFocusedId = expectedFocusedId;
    }

    public Boolean getCheckRoleAttribute() {
        return checkRoleAttribute;
    }

    public void setCheckRoleAttribute(Boolean checkRoleAttribute) {
        this.checkRoleAttribute = checkRoleAttribute;
    }

    public String getExpectedRole() {
        return expectedRole;
    }

    public void setExpectedRole(String expectedRole) {
        this.expectedRole = expectedRole;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}