package utils;

import java.util.List;

public class TestCase {
    private String question;
    private List<String> expected_keywords;
    private int min_keyword_match;

    // Getters
    public String getQuestion() {
        return question;
    }

    public List<String> getExpected_keywords() {
        return expected_keywords;
    }

    public int getMin_keyword_match() {
        return min_keyword_match;
    }
}