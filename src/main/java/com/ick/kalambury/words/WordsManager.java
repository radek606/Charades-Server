package com.ick.kalambury.words;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ick.kalambury.storage.TableInstanceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WordsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordsManager.class);

    private final ObjectMapper objectMapper;
    private final TableInstanceDao tableInstanceRepository;
    private final WordsFileManager manager;
    private final WordMatcher wordMatcher;

    private final Map<String, WordsSetInfo> setsInfoMap;
    private final Map<String, WordsInstance> instancesMap;

    private final Random random;

    @Autowired
    public WordsManager(ObjectMapper objectMapper, TableInstanceDao tableInstanceRepository,
                        WordsFileManager manager, WordMatcher wordMatcher) {
        this.objectMapper = objectMapper;
        this.tableInstanceRepository = tableInstanceRepository;
        this.manager = manager;
        this.wordMatcher = wordMatcher;
        this.random = new Random();
        this.setsInfoMap = new HashMap<>();
        this.instancesMap = new HashMap<>();
    }

    @PostConstruct
    private void init() throws IOException {
        WordsManifest manifest = objectMapper.readValue(manager.getWordsManifest().getFile(), WordsManifest.class);
        setsInfoMap.putAll(manifest.getSets().stream()
                .collect(Collectors.toMap(WordsManifest.Set::getId, WordsSetInfo::new)));
    }

    public void registerTable(String id, Set<String> selectedSets) {
        WordsInstance instance = tableInstanceRepository.get(id).orElseGet(() -> createInstance(id, selectedSets));
        updateInstanceIfNeeded(instance, selectedSets);
        instancesMap.put(id, instance);
    }

    private WordsInstance createInstance(String instanceId, Set<String> selectedSets) {
        List<WordsSet> sets = new ArrayList<>();
        for (String setId : selectedSets) {
            sets.add(loadWordsSet(setId));
        }
        return new WordsInstance(instanceId, sets, selectedSets);
    }

    private void updateInstanceIfNeeded(WordsInstance instance, Set<String> newSelectedSets) {
        Set<String> oldSelectedSets = instance.getSelectedSets();

        Set<String> setIdsToRemove = oldSelectedSets.stream()
                .filter(s -> !newSelectedSets.contains(s))
                .collect(Collectors.toSet());
        Set<String> setIdsToAdd = newSelectedSets.stream()
                .filter(s -> !oldSelectedSets.contains(s))
                .collect(Collectors.toSet());

        instance.getWordsSets().removeIf(s -> setIdsToRemove.contains(s.getId()));
        for (String id : setIdsToAdd) {
            instance.getWordsSets().add(loadWordsSet(id));
        }
        instance.setSelectedSets(newSelectedSets);
    }

    private void resetInstance(WordsInstance instance) {
        for (String id : instance.getSelectedSets()) {
            instance.getWordsSets().add(loadWordsSet(id));
        }
    }

    public void saveTable(String id) {
        if (instancesMap.get(id) != null) {
            tableInstanceRepository.set(instancesMap.get(id));
        }
    }

    public Word drawWord(String tableId) {
        WordsInstance instance = instancesMap.get(tableId);

        Word word = instance.getNextRandomWord(random);
        word.setSetName(setsInfoMap.get(word.getSetName()).getName());

        if (!instance.hasWords()) {
            resetInstance(instance);
        }

        return word;
    }

    public WordMatchingResult matchWord(Word word, String answer) {
        return wordMatcher.matchAnswer(word, answer);
    }

    private WordsSet loadWordsSet(String setId) {
        try {
            return objectMapper.readValue(manager.getWordsSetPlain(setId).getFile(), WordsSet.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    private void deinit() {
        tableInstanceRepository.setAll(instancesMap);
    }

}
