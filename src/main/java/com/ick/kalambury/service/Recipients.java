package com.ick.kalambury.service;

import com.ick.kalambury.entities.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Recipients {

    public static List<String> one(Player player) {
        return Collections.singletonList(player.getId());
    }

    public static List<String> all(Collection<Player> players) {
        return players.stream()
                .filter(Player::isActive)
                .map(Player::getId)
                .collect(Collectors.toList());
    }

    public static List<String> allExcept(Collection<Player> players, Player except) {
        return players.stream()
                .filter(player -> player.isActive() && !player.equals(except))
                .map(Player::getId)
                .collect(Collectors.toList());
    }

}
