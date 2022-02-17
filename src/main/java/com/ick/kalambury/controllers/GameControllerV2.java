package com.ick.kalambury.controllers;

import com.ick.kalambury.api.TablesDto;
import com.ick.kalambury.api.converters.TablesConverter;
import com.ick.kalambury.security.AuthenticationFacade;
import com.ick.kalambury.service.GameService;
import com.ick.kalambury.storage.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2/game")
public class GameControllerV2 extends GameControllerBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameControllerV2.class);

    @Autowired
    public GameControllerV2(AuthenticationFacade authentication, UserDao userRepository, GameService service) {
        super(authentication, userRepository, service);
    }

    @GetMapping("/tables")
    public TablesDto getTables() {
        return new TablesConverter().convert(service.getTables());
    }

}
