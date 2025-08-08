package com.destroflyer.escapeloop.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class CinematicAction {

    @Getter
    private float time;
    private Runnable runnable;

    public void run() {
        runnable.run();
    }
}
