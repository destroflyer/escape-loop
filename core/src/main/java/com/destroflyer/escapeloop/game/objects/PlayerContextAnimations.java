package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.destroflyer.escapeloop.util.TextureUtil;

import java.util.function.Function;

import lombok.Getter;

@Getter
public class PlayerContextAnimations {

    public PlayerContextAnimations(boolean withTimeMachine) {
        Function<String, String> getTexturePath = name -> "./textures/player_robot/orange/" + name + "_" + (withTimeMachine ? "with" : "without") + "_time_machine.png";
        idleAnimation = TextureUtil.loadWrappedAnimation(getTexturePath.apply("idle"), 4, 2, 5, 0.2f);
        runAnimation = TextureUtil.loadWrappedAnimation(getTexturePath.apply("run"), 5, 1, 0.15f);
        flyingAnimation = TextureUtil.loadWrappedAnimation(getTexturePath.apply("flying"), 2, 1, 0.2f);
    }
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> flyingAnimation;
}
