package com.ick.kalambury.words;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Word {

    @JsonProperty("text")
    private List<String> variants;

    @JsonIgnore
    private String setName;

    public Word() {
    }

    public Word(List<String> variants) {
        this.variants = variants;
    }

    @JsonIgnore
    public String getWord() {
        return variants.get(0);
    }

    public List<String> getVariants() {
        return variants;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

}
