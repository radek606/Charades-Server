package com.ick.kalambury.security;

import com.ick.kalambury.entities.User;
import com.ick.kalambury.storage.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UserDao userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<User> optionalUser = userRepository.get(authentication.getName());
        if (!optionalUser.isPresent()) {
            throw new BadCredentialsException("Invalid username or password");
        }

        final Authentication result = super.authenticate(authentication);
        return new UsernamePasswordAuthenticationToken(optionalUser.get(), result.getCredentials(), result.getAuthorities());
    }

}
