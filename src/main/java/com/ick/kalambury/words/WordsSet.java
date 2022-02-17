package com.ick.kalambury.words;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordsSet {

    @JsonProperty
    private String id;

    @JsonProperty
    private List<Word> words = new ArrayList<>();

    public WordsSet() {
    }

    public Word getRandomWord(Random random) {
        return words.remove(random.nextInt(words.size()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public boolean hasWords() {
        return !words.isEmpty();
    }

}
