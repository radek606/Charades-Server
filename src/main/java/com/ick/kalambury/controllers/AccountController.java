package com.ick.kalambury.controllers;

import com.ick.kalambury.api.ChangedPasswordDto;
import com.ick.kalambury.api.RegistrationParametersDto;
import com.ick.kalambury.api.ResetPasswordDto;
import com.ick.kalambury.exceptions.InvalidEmailException;
import com.ick.kalambury.security.AuthenticationFacade;
import com.ick.kalambury.service.UserService;
import com.ick.kalambury.validation.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@ConditionalOnExpression("false")
@RequestMapping("/v1/account")
public class AccountController extends BaseController {

    private final UserService userService;

    @Autowired
    public AccountController(AuthenticationFacade authentication, UserService userService) {
        super(authentication);
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void register(@RequestBody @Valid RegistrationParametersDto parameters) {
        userService.register(parameters);
    }

    @PutMapping("/setEmail")
    @ResponseStatus(code = HttpStatus.OK)
    public void setEmail(@RequestParam("email") String email) {
        if (!Utils.isValidEmail(email)) {
            throw new InvalidEmailException(email);
        }

        userService.setEmail(getPrincipal(), email);
    }

    @PostMapping("/resetPassword")
    @ResponseStatus(code = HttpStatus.OK)
    public void resetPassword(@RequestParam("email") String email) {
        if (!Utils.isValidEmail(email)) {
            throw new InvalidEmailException(email);
        }

        userService.createPasswordResetToken(email);
    }

    @PostMapping("/changePassword")
    @ResponseStatus(code = HttpStatus.OK)
    public void changePassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto) {
        userService.resetPassword(resetPasswordDto);
    }

    @PutMapping("/changePassword")
    @ResponseStatus(code = HttpStatus.OK)
    public void changePassword(@RequestBody ChangedPasswordDto changedPasswordDto) {
        userService.changePassword(getPrincipal(), changedPasswordDto);
    }

}
