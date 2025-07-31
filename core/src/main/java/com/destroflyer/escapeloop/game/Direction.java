package com.destroflyer.escapeloop.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Direction {
    LEFT(-1, 0),
    RIGHT(1, 0),
    DOWN(0, -1),
    UP(0, 1);

    private int x;
    private int y;
}
