package com.ick.kalambury.words;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class WordsManifest {

    @JsonProperty
    private List<Set> sets;

    public WordsManifest() { }

    public List<Set> getSets() {
        return sets;
    }

    @Override
    public String toString() {
        return "WordsManifest{" +
                ", sets=" + sets +
                '}';
    }

    public static class Set {

        @JsonProperty
        private String id;

        @JsonProperty
        private String name;

        public Set() { }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Set{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }

    }

}
