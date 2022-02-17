package com.ick.kalambury.security;

import com.ick.kalambury.entities.User;
import com.ick.kalambury.storage.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class KalamburyUserService implements UserDetailsService {

    private final UserDao userRepository;

    @Autowired
    public KalamburyUserService(UserDao userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.get(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " was not found"));

        return new org.springframework.security.core.userdetails.User(user.getId(), user.getPassword(),
                    user.getRole().asGrantedAuthority());
    }

}
