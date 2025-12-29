package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.destroflyer.escapeloop.game.Collisions;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.util.TextureUtil;

import lombok.Getter;
import lombok.Setter;

public class Player extends Character {

    public Player() {
        textureOffset = new Vector2(0, 0.25f);
        textureSize = new Vector2(1, 1);
        removalAnimation = ANIMATION_REMOVAL;
        removalSound = "explosion";
    }
    public static final PlayerContextAnimations ANIMATIONS_WITH_TIME_MACHINE = new PlayerContextAnimations(true);
    private static final PlayerContextAnimations ANIMATIONS_WITHOUT_TIME_MACHINE = new PlayerContextAnimations(false);
    private static final Animation<TextureRegion> ANIMATION_ACTION_HORIZONTAL = TextureUtil.loadWrappedAnimation("./textures/player_robot/orange/action_horizontal.png", 2, 2, 0.1f);
    private static final Animation<TextureRegion> ANIMATION_ACTION_VERTICAL = TextureUtil.loadWrappedAnimation("./textures/player_robot/orange/action_vertical.png", 2, 2, 0.1f);
    private static final Animation<TextureRegion> ANIMATION_REMOVAL = TextureUtil.loadWrappedAnimation("./textures/player_robot/orange/death.png", 2, 2, 0.1f);
    @Setter
    private boolean hasTimeMachine = true;
    @Getter
    private boolean characterCollisionsEnabled;
    private boolean hasSetWalkDirection;

    @Override
    public void createBody() {
        super.createBody();
        characterFixture.getFilterData().categoryBits |= Collisions.PLAYER;
        characterFixture.getFilterData().maskBits |= Collisions.TIME_MACHINE;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        // Keep collisions between players disabled for a short start duration, to make the spawn feel more fluid
        // Also, as long as they don't start walking, collisions are disabled, to avoid a sudden confusing bounce
        if (!characterCollisionsEnabled && ((hasSetWalkDirection && (map.getTime() > 0.75f)) || (activeContacts.stream().noneMatch(mapObject -> mapObject instanceof Character)))) {
            characterCollisionsEnabled = true;
        }
    }

    @Override
    public void preSolve(MapObject mapObject, Fixture ownFixture, Fixture otherFixture, Contact contact) {
        super.preSolve(mapObject, ownFixture, otherFixture, contact);
        if (mapObject instanceof Character) {
            boolean solveCollision = characterCollisionsEnabled;
            if (solveCollision && (mapObject instanceof Player)) {
                Player player = (Player) mapObject;
                if (player.isCharacterCollisionsEnabled()) {
                    // Because this is inside preSolve, it needs to be enqueued (to avoid non-deterministic physics)
                    map.queueTask(() -> bounceOff(player));
                }
                solveCollision = false;
            }
            contact.setEnabled(solveCollision);
        }
    }

    @Override
    public void setWalkDirection(int walkDirection) {
        super.setWalkDirection(walkDirection);
        hasSetWalkDirection = true;
    }

    @Override
    public void action() {
        super.action();
        setOneTimeAnimation((verticalDirection != 0) ? ANIMATION_ACTION_VERTICAL : ANIMATION_ACTION_HORIZONTAL);
    }

    @Override
    protected Animation<TextureRegion> getLoopedAnimation() {
        PlayerContextAnimations animations = (hasTimeMachine ? ANIMATIONS_WITH_TIME_MACHINE : ANIMATIONS_WITHOUT_TIME_MACHINE);
        if (!isOnGround()) {
            return animations.getFlyingAnimation();
        }
        return (walkDirection != 0) ? animations.getRunAnimation() : animations.getIdleAnimation();
    }
}
