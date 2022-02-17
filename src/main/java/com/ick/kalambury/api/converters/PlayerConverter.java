package com.ick.kalambury.api.converters;

import com.ick.kalambury.api.PlayerDto;
import com.ick.kalambury.entities.Player;

public class PlayerConverter implements BaseConverter<Player, PlayerDto> {

    @Override
    public PlayerDto convert(Player from) {
        return new PlayerDto(from.getId(), from.getUser().getNickname(), null);
    }

}
