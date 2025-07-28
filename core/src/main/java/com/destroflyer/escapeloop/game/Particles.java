package com.destroflyer.escapeloop.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Particles {
    CIRCLE(5),
    UP(1),
    DOWN(1);

    private float duration;
}
