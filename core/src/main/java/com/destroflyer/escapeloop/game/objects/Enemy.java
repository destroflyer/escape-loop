package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.destroflyer.escapeloop.util.TextureUtil;

public class Enemy extends Character {

    public Enemy() {
        textureSize = new Vector2(1, 1);
    }
    private static final Animation<TextureRegion> ANIMATION_IDLE = TextureUtil.loadAnimation("./textures/enemy_robot/idle.png", 2, 2, 4, 0.2f);
    private static final Animation<TextureRegion> ANIMATION_RUN = TextureUtil.loadAnimation("./textures/enemy_robot/run.png", 2, 2, 0.15f);

    @Override
    protected Animation<TextureRegion> getLoopedAnimation() {
        return (walkDirection != 0) ? ANIMATION_RUN : ANIMATION_IDLE;
    }
}
