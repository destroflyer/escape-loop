package com.destroflyer.escapeloop.game.replays.json;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class ReplayMetadata {
    private int fps;
    private float tpf;
    private String skinPlayer;
    private String skinEnemy;
}
