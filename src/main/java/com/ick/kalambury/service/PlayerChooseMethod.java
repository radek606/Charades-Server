package com.ick.kalambury.service;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum PlayerChooseMethod {

    GUESSING_PLAYER, LONGEST_WAITING_PLAYER, RANDOM_PLAYER;

}
