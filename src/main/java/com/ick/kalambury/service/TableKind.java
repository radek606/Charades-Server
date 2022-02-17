package com.ick.kalambury.service;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TableKind {
    DEFAULT, PUBLIC, PRIVATE
}
