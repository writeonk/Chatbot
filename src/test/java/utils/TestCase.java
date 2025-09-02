package utils;

import java.util.List;

public class TestCase {
    private String id;
    private String question;
    private List<String> expected_keywords;       // Accuracy
    private int min_keyword_match;               // Accuracy
    private List<String> blacklist;              // Hallucination
    private List<String> consistencyPhrases;     // Consistency (EN + AR)
    private List<String> fallbackMessages;       // Fallback / loading
    private boolean strictHallucination = true;  // optional: fail or warn

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

    public List<String> getConsistencyPhrases() {
        return consistencyPhrases;
    }

    public List<String> getFallbackMessages() {
        return fallbackMessages;
    }

    public boolean isStrictHallucination() {
        return strictHallucination;
    }

}