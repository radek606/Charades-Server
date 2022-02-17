package com.ick.kalambury.controllers;

import com.ick.kalambury.security.AuthenticationFacade;
import com.ick.kalambury.words.WordsFileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/words")
public class WordsController extends BaseController {

    private final WordsFileManager manager;

    @Autowired
    public WordsController(AuthenticationFacade authenticationFacade, WordsFileManager manager) {
        super(authenticationFacade);
        this.manager = manager;
    }

    @GetMapping(value = "/{setId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public FileSystemResource getWordsSet(@PathVariable String setId) {
        return manager.getWordsSetEncrypted(setId);
    }

}
