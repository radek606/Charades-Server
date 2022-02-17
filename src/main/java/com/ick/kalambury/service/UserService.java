package com.ick.kalambury.service;

import com.ick.kalambury.api.ChangedPasswordDto;
import com.ick.kalambury.api.RegistrationParametersDto;
import com.ick.kalambury.api.ResetPasswordDto;
import com.ick.kalambury.entities.PasswordResetToken;
import com.ick.kalambury.entities.User;
import com.ick.kalambury.exceptions.*;
import com.ick.kalambury.security.Role;
import com.ick.kalambury.storage.PasswordTokenDao;
import com.ick.kalambury.storage.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserDao userRepository;
    private final PasswordTokenDao passwordTokenDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDao userRepository, PasswordTokenDao passwordTokenDao, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordTokenDao = passwordTokenDao;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getUserByNickname(String nickname) {
        return userRepository.get(nickname);
    }

    public void register(RegistrationParametersDto parameters) {
        if (userRepository.get(parameters.getNickname()).isPresent()) {
            throw new UserAlreadyRegisteredException(parameters.getNickname());
        }

        if (!parameters.getPassword().equals(parameters.getMatchingPassword())) {
            throw new PasswordNotMatchesException();
        }

        User.Builder builder = User.newBuilder(passwordEncoder)
                .setNickname(parameters.getNickname())
                .setPassword(parameters.getPassword())
                .setRole(Role.GUEST);

        if (!StringUtils.isEmpty(parameters.getEmail())) {
            builder.setEmail(parameters.getEmail());
        }

        userRepository.set(builder.build());
    }

    public void setEmail(User user, String email) {
        userRepository.set(user.toBuilder(passwordEncoder)
                .setEmail(email)
                .build());
    }

    public void changePassword(User user, ChangedPasswordDto changedPasswordDto) {
        if (passwordEncoder.matches(changedPasswordDto.getPassword(), user.getPassword())) {
            throw new InvalidOldPasswordException();
        }

        if (!changedPasswordDto.getPassword().equals(changedPasswordDto.getMatchingPassword())) {
            throw new PasswordNotMatchesException();
        }

        userRepository.set(user.toBuilder(passwordEncoder)
                .setPassword(changedPasswordDto.getPassword())
                .build());
    }

    public void createPasswordResetToken(String email) {
        User user = getUserByEmail(email).orElseThrow(EmailNotFoundException::new);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken(user.getNickname(), token);
        passwordTokenDao.set(resetToken);
    }

    public void resetPassword(@Valid ResetPasswordDto resetPasswordDto) {
        PasswordResetToken passToken = passwordTokenDao.get(resetPasswordDto.getResetToken())
                .orElseThrow(InvalidPasswordTokenException::new);

        if (passToken.getExpiryDate().before(new Date())) {
            throw new ExpiredPasswordTokenException();
        }

        if (!resetPasswordDto.getPassword().equals(resetPasswordDto.getMatchingPassword())) {
            throw new PasswordNotMatchesException();
        }

        User user = userRepository.get(passToken.getUsername()).orElseThrow(UserNotFoundException::new);
        user = user.toBuilder(passwordEncoder)
                .setPassword(resetPasswordDto.getPassword())
                .build();

        userRepository.set(user);
        passwordTokenDao.delete(passToken.getToken());
    }

    /**
     * For special purposes only, like: password resetting.
     * @param email
     * @return
     */
    public Optional<User> getUserByEmail(@NonNull String email) {
        return userRepository.getEntries()
                .values()
                .stream()
                .filter(user -> passwordEncoder.matches(email, user.getEmail()))
                .findFirst();
    }

}
