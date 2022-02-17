package com.ick.kalambury.controllers;

import com.ick.kalambury.config.Parameters;
import com.ick.kalambury.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/v1/log")
public class LogController extends BaseController {

    private final Parameters parameters;

    @Autowired
    public LogController(AuthenticationFacade authenticationFacade, Parameters parameters) {
        super(authenticationFacade);
        this.parameters = parameters;
    }

    @PostMapping("/submit")
    @ResponseStatus(code = HttpStatus.OK)
    public void submit(@RequestPart("file") MultipartFile file) throws IOException {
        Path path = Paths.get(parameters.getLogStorage().getPath()).toAbsolutePath().normalize();
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, path.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
