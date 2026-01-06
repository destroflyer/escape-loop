package com.destroflyer.escapeloop.game.animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.destroflyer.escapeloop.game.Skin;
import com.destroflyer.escapeloop.game.Skins;
import com.destroflyer.escapeloop.util.TextureUtil;

import java.util.HashMap;

import lombok.Getter;

@Getter
public class PlayerAnimations {

    private static final HashMap<String, PlayerAnimations> ANIMATIONS = new HashMap<>();
    static {
        for (Skin skin : Skins.PLAYER) {
            ANIMATIONS.put(skin.getName(), new PlayerAnimations(skin));
        }
    }

    public static PlayerAnimations get(Skin skin) {
        return ANIMATIONS.get(skin.getName());
    }

    private PlayerAnimations(Skin skin) {
        animationsWithoutTimeMachine = new PlayerContextAnimations(skin, 0, 2, 4);
        animationsWithTimeMachine = new PlayerContextAnimations(skin, 1, 3, 5);
        actionHorizontalAnimation = TextureUtil.getPlayerAnimation(skin, 6, 4, 0.1f);
        actionVerticalAnimation = TextureUtil.getPlayerAnimation(skin, 7, 4, 0.1f);
        removalAnimation = TextureUtil.getPlayerAnimation(skin, 8, 4, 0.1f);
    }
    private PlayerContextAnimations animationsWithoutTimeMachine;
    private PlayerContextAnimations animationsWithTimeMachine;
    private Animation<TextureRegion> actionHorizontalAnimation;
    private Animation<TextureRegion> actionVerticalAnimation;
    private Animation<TextureRegion> removalAnimation;
}
