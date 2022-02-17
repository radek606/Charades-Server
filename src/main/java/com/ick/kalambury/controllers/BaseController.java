package com.ick.kalambury.controllers;

import com.ick.kalambury.entities.User;
import com.ick.kalambury.security.AuthenticationFacade;
import org.springframework.security.core.Authentication;

public abstract class BaseController {

    private final AuthenticationFacade authenticationFacade;

    BaseController(AuthenticationFacade authenticationFacade) {
        this.authenticationFacade = authenticationFacade;
    }

    protected Authentication getAuthentication() {
        return authenticationFacade.getAuthentication();
    }

    protected void setAuthentication(Authentication authentication) {
        authenticationFacade.setAuthentication(authentication);
    }

    protected User getPrincipal() {
        return (User) authenticationFacade.getAuthentication().getPrincipal();
    }
}
