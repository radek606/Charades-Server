package com.ick.kalambury.words;

import com.ick.kalambury.config.Parameters;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.Map;

@Component
public class WordMatcher {

    private static final Map<Character, Character> charsMapping;
    static {
        charsMapping = new Hashtable<>();
        charsMapping.put('ą', 'a');
        charsMapping.put('ć', 'c');
        charsMapping.put('ę', 'e');
        charsMapping.put('ł', 'l');
        charsMapping.put('ń', 'n');
        charsMapping.put('ó', 'o');
        charsMapping.put('ś', 's');
        charsMapping.put('ź', 'z');
        charsMapping.put('ż', 'z');
    }

    private final Parameters.GameConfig parameters;

    public WordMatcher(Parameters parameters) {
        this.parameters = parameters.getGameConfig();
    }

    public WordMatchingResult matchAnswer(Word word, String answer) {
        String matchedVariant = getExactMatch(word, answer);
        if (matchedVariant != null) {
            return new WordMatchingResult(matchedVariant, answer, true, true);
        }

        String[] splitAnswer = answer.split("\\s");
        for (String variant : word.getVariants()) {
            String[] splitVariant = variant.split("\\s");

            if (splitAnswer.length > splitVariant.length) break;

            for (String variantPart : splitVariant) {
                if (!isValidWord(variantPart)) continue;

                String normalizedVariantPart = normalizeString(variantPart);
                int maxDistance = Math.max(1, Math.round(normalizedVariantPart.length() * parameters.getCloseEnoughAnswerDistanceFactor()));

                for (String answerPart : splitAnswer) {
                    if (!isValidPair(variantPart, answerPart)) continue;

                    String normalizedAnswerPart = normalizeString(answerPart);
                    int distance = levenshteinDistance(normalizedVariantPart, normalizedAnswerPart);
                    if (distance <= maxDistance) {
                        return new WordMatchingResult(variant, answer, false, true);
                    }
                }
            }
        }

        return new WordMatchingResult(word.getWord(), answer, false, false);
    }

    private String getExactMatch(Word word, String answer) {
        String normalizedAnswer = normalizeString(answer);
        for (String variant : word.getVariants()) {
            String normalizedVariant = normalizeString(variant);

            if (normalizedVariant.equals(normalizedAnswer)) {
                return variant;
            }
        }
        return null;
    }

    private boolean isValidPair(String variantWord, String answerWord) {
        int variantLength = variantWord.length();
        int answerLength = answerWord.length();

        if (variantLength <= 2 || answerLength <= 2) {
            return false;
        }

        int lengthDiff = Math.abs(variantLength - answerLength);

        //for 3-letter words length diff must be 0
        if (variantLength == 3 && lengthDiff != 0) {
            return false;
        }

        int maxLengthDiff = Math.max(1, Math.round(variantWord.length() * parameters.getWordsPairLengthDiffFactor()));

        return lengthDiff <= maxLengthDiff;
    }

    private boolean isValidWord(String word) {
        return word.length() > 2;
    }

    private String normalizeString(String string) {
        return replaceChars(string.toLowerCase().replaceAll("\\s",""));
    }

    private String replaceChars(String word) {
        for (Map.Entry<Character, Character> entry : charsMapping.entrySet()) {
            word = word.replace(entry.getKey(), entry.getValue());
        }
        return word;
    }

    //https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
    private int levenshteinDistance(String lhs, String rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        for (int i = 0; i < len0; i++) cost[i] = i;

        for (int j = 1; j < len1; j++) {
            newcost[0] = j;

            for(int i = 1; i < len0; i++) {
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                int cost_replace = cost[i - 1] + match;
                int cost_insert  = cost[i] + 1;
                int cost_delete  = newcost[i - 1] + 1;

                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        return cost[len0 - 1];
    }
}
