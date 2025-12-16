package com.destroflyer.escapeloop.states.models;

import com.destroflyer.escapeloop.game.replays.json.Replay;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Highscore {
    private String user;
    private int frames;
    private Replay replay;
}
