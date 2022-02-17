package com.ick.kalambury.words;

public class WordsSetInfo {

    private final String id;
    private final String name;

    public WordsSetInfo(WordsManifest.Set set) {
        this.id = set.getId();
        this.name = set.getName();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "WordsSetInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
