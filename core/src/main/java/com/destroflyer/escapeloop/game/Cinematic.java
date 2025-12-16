package com.destroflyer.escapeloop.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import lombok.Getter;

public class Cinematic {

    public Cinematic(PlayMap map) {
        this.map = map;
    }
    protected static final float SPEECH_DURATION_SHORT = 1;
    protected static final float SPEECH_DURATION_MEDIUM = 2.2f;
    protected static final float SPEECH_DURATION_LONG = 3.3f;
    protected static final float SPEECH_BREAK_SHORT = 0.2f;
    protected static final float SPEECH_BREAK_LONG = 0.5f;
    protected PlayMap map;
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

    protected Vector2 getTileCenter(float tileX, float tileY) {
        return new Vector2((tileX + 0.5f) * Map.TILE_SIZE, (tileY + 0.5f) * Map.TILE_SIZE);
    }

    public void onPauseMenuOpen() {

    }

    public void onPauseMenuClose() {

    }

    public void finish() {

    }

    public void updateRenderBounds(Rectangle bounds) {

    }

    public void render(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {

    }
}
