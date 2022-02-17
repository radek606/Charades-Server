package com.ick.kalambury.words;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ick.kalambury.storage.RedisSimpleEntity;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class WordsInstance implements RedisSimpleEntity {

    @JsonProperty
    private String id;

    @JsonProperty
    private Set<String> selectedSets;

    @JsonProperty
    private List<WordsSet> wordsSets;

    @JsonIgnore
    private Word currentWord;

    public WordsInstance() {
    }

    public WordsInstance(String id, List<WordsSet> wordsSets, Set<String> selectedSets) {
        this.id = id;
        this.wordsSets = wordsSets;
        this.selectedSets = selectedSets;
    }

    @Override
    public String getId() {
        return id;
    }

    public Word getCurrentWord() {
        return currentWord;
    }

    public Word getNextRandomWord(Random random) {
        int index = random.nextInt(wordsSets.size());

        WordsSet set = wordsSets.get(index);
        currentWord = set.getRandomWord(random);
        currentWord.setSetName(set.getId());

        if (!set.hasWords()) {
            wordsSets.remove(index);
        }

        return currentWord;
    }

    public Set<String> getSelectedSets() {
        return selectedSets;
    }

    public void setSelectedSets(Set<String> selectedSets) {
        this.selectedSets = selectedSets;
    }

    public boolean hasWords() {
        return !wordsSets.isEmpty();
    }

    public List<WordsSet> getWordsSets() {
        return wordsSets;
    }
}
