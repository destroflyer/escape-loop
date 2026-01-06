package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.destroflyer.escapeloop.util.TextureUtil;

public class Scientist extends Character {

    public Scientist() {
        textureOffset = new Vector2(0, 0.18f);
        textureSize = new Vector2(0.85f, 0.85f);
    }
    private static final Animation<TextureRegion> ANIMATION_IDLE = TextureUtil.getScientistAnimation(0, 4, 0.25f);
    private static final Animation<TextureRegion> ANIMATION_RUN = TextureUtil.getScientistAnimation(1, 8, 0.15f);
    private static final Animation<TextureRegion> ANIMATION_FLYING = TextureUtil.getScientistAnimation(2, 4, 0.2f);

    @Override
    protected Animation<TextureRegion> getLoopedAnimation() {
        if (!isOnGround()) {
            return ANIMATION_FLYING;
        }
        return (walkDirection != 0) ? ANIMATION_RUN : ANIMATION_IDLE;
    }
}
