package com.ick.kalambury.controllers;

import com.ick.kalambury.security.AuthenticationFacade;
import com.ick.kalambury.service.GameService;
import com.ick.kalambury.storage.UserDao;

public class GameControllerBase extends BaseController {

    final UserDao userRepository;
    final GameService service;

    GameControllerBase(AuthenticationFacade authentication, UserDao userRepository, GameService service) {
        super(authentication);
        this.userRepository = userRepository;
        this.service = service;
    }

}
