package com.destroflyer.escapeloop.game.animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.destroflyer.escapeloop.game.Skin;
import com.destroflyer.escapeloop.util.TextureUtil;

import lombok.Getter;

@Getter
public class PlayerContextAnimations {

    public PlayerContextAnimations(Skin skin, int idleRow, int runRow, int flyingRow) {
        idleAnimation = TextureUtil.getPlayerAnimation(skin, idleRow, 5, 0.2f);
        runAnimation = TextureUtil.getPlayerAnimation(skin, runRow, 5, 0.15f);
        flyingAnimation = TextureUtil.getPlayerAnimation(skin, flyingRow, 2, 0.2f);
    }
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> flyingAnimation;
}
