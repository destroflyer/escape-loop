package com.destroflyer.escapeloop.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QueuedTask {

    private Runnable runnable;
    private float remainingTime;

    public void update(float tpf) {
        remainingTime -= tpf;
    }

    public boolean shouldRun() {
        return remainingTime <= 0;
    }

    public void run() {
        runnable.run();
    }
}
