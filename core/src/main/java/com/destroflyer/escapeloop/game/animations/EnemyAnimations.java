package com.destroflyer.escapeloop.game.animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.destroflyer.escapeloop.game.Skin;
import com.destroflyer.escapeloop.game.Skins;
import com.destroflyer.escapeloop.util.TextureUtil;

import java.util.HashMap;

import lombok.Getter;

@Getter
public class EnemyAnimations {

    private static final HashMap<String, EnemyAnimations> ANIMATIONS = new HashMap<>();
    static {
        for (Skin skin : Skins.ENEMY) {
            ANIMATIONS.put(skin.getName(), new EnemyAnimations(skin));
        }
    }

    public static EnemyAnimations get(Skin skin) {
        return ANIMATIONS.get(skin.getName());
    }

    private EnemyAnimations(Skin skin) {
        idleAnimation = TextureUtil.loadEnemyAnimation(skin, 0, 4, 0.2f);
        runAnimation = TextureUtil.loadEnemyAnimation(skin, 1, 4, 0.15f);
        shootAnimation = TextureUtil.loadEnemyAnimation(skin, 2, 2, 0.05f);
        removalAnimation = TextureUtil.loadEnemyAnimation(skin, 3, 3, 0.05f);
        bulletFlyingAnimation = TextureUtil.loadEnemyAnimation(skin, 4, 3, 0.2f);
    }
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> shootAnimation;
    private Animation<TextureRegion> removalAnimation;
    private Animation<TextureRegion> bulletFlyingAnimation;
}
