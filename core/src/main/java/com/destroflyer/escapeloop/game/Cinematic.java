package com.destroflyer.escapeloop.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

import lombok.Getter;

public class Cinematic {

    public Cinematic(Map map) {
        this.map = map;
    }
    protected Map map;
    @Getter
    protected float duration;
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

    public void finish() {

    }

    public void updateRenderBounds(Rectangle bounds) {

    }

    public void render(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {

    }
}
