package com.ick.kalambury.controllers;

import com.ick.kalambury.security.AuthenticationFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController extends BaseController {

    MainController(AuthenticationFacade authenticationFacade) {
        super(authenticationFacade);
    }

    @GetMapping("")
    public String mainPage() {
        return "redirect:https://play.google.com/store/apps/details?id=com.ick.kalambury";
    }

}
