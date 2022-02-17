package com.ick.kalambury.words;

public class WordMatchingResult {

    private final String word;
    private final String answer;

    private final boolean match;
    private final boolean closeEnough;

    WordMatchingResult(String word, String answer, boolean match, boolean closeEnough) {
        this.word = word;
        this.answer = answer;
        this.match = match;
        this.closeEnough = closeEnough;
    }

    public String getWord() {
        return word;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isMatch() {
        return match;
    }

    public boolean isCloseEnough() {
        return closeEnough;
    }

}
