package com.ick.kalambury.words;

import com.ick.kalambury.config.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
public class WordsFileManager {

    private static final String LOCAL_MANIFEST_FILE = "manifest.json";

    private final Parameters.WordsStorage wordsStorage;

    @Autowired
    public WordsFileManager(Parameters parameters) {
        this.wordsStorage = parameters.getWordsStorage();
    }

    public FileSystemResource getWordsManifest() {
        return new FileSystemResource(Paths.get(wordsStorage.getPath(), LOCAL_MANIFEST_FILE));
    }

    public FileSystemResource getWordsSetPlain(String setId) {
        return new FileSystemResource(Paths.get(wordsStorage.getPath(), setId, wordsStorage.getPlaintextFile()));
    }

    public FileSystemResource getWordsSetEncrypted(String setId) {
        return new FileSystemResource(Paths.get(wordsStorage.getPath(), setId, wordsStorage.getEncryptedFile()));
    }

}
