package com.destroflyer.escapeloop.game;

import java.util.ArrayList;

public class Cinematic {

    public Cinematic(Map map) {
        this.map = map;
    }
    protected Map map;
    private ArrayList<CinematicAction> actions = new ArrayList<>();

    public void add(float time, Runnable runnable) {
        actions.add(new CinematicAction(time, runnable));
    }

    public void applyTime(float time) {
        for (int i = 0; i < actions.size(); i++) {
            CinematicAction action = actions.get(i);
            if (time >= action.getTime()) {
                action.run();
                actions.remove(i);
                i--;
            }
        }
    }
}
