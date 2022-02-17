package com.ick.kalambury.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.LinkedList;
import java.util.List;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Role {

    ADMIN,
    USER,
    GUEST;

    public List<GrantedAuthority> asGrantedAuthority() {
        List<GrantedAuthority> authorities = new LinkedList<>();
        authorities.add(new SimpleGrantedAuthority(name()));
        return authorities;
    }

}
