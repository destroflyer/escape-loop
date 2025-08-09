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

public class Player extends Character {

    public Player() {
        textureOffset = new Vector2(0, 0.25f);
        textureSize = new Vector2(1, 1);
    }
    private static final Animation<TextureRegion> ANIMATION_IDLE = TextureUtil.loadWrappedAnimation("./textures/orange_robot/idle.png", 4, 2, 5, 0.2f);
    public static final Animation<TextureRegion> ANIMATION_RUN = TextureUtil.loadWrappedAnimation("./textures/orange_robot/run.png", 5, 1, 0.15f);
    private static final Animation<TextureRegion> ANIMATION_FLYING = TextureUtil.loadWrappedAnimation("./textures/orange_robot/flying.png", 2, 1, 0.2f);
    private static final Animation<TextureRegion> ANIMATION_ACTION_HORIZONTAL = TextureUtil.loadWrappedAnimation("./textures/orange_robot/action_horizontal.png", 2, 2, 0.1f);
    private static final Animation<TextureRegion> ANIMATION_ACTION_VERTICAL = TextureUtil.loadWrappedAnimation("./textures/orange_robot/action_vertical.png", 2, 2, 0.1f);
    @Getter
    private boolean characterCollisionsEnabled;

    @Override
    public void createBody() {
        super.createBody();
        characterFixture.getFilterData().categoryBits |= Collisions.PLAYER;
        characterFixture.getFilterData().maskBits |= Collisions.TIME_MACHINE;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (!characterCollisionsEnabled && activeContacts.stream().noneMatch(mapObject -> mapObject instanceof Character)) {
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
                    bounceOff(player);
                }
                solveCollision = false;
            }
            contact.setEnabled(solveCollision);
        }
    }

    @Override
    public void action() {
        super.action();
        setOneTimeAnimation((verticalDirection != 0) ? ANIMATION_ACTION_VERTICAL : ANIMATION_ACTION_HORIZONTAL);
    }

    @Override
    public void setWalkDirection(int walkDirection) {
        super.setWalkDirection(walkDirection);
        characterCollisionsEnabled = true;
    }

    @Override
    protected Animation<TextureRegion> getLoopedAnimation() {
        if (!isOnGround()) {
            return ANIMATION_FLYING;
        }
        return (walkDirection != 0) ? ANIMATION_RUN : ANIMATION_IDLE;
    }
}
