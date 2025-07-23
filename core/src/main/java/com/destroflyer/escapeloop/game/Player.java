package com.destroflyer.escapeloop.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.destroflyer.escapeloop.util.TextureUtil;

import lombok.Getter;

public class Player extends Character {

    public Player() {
        textureOffset = new Vector2(0, 0.25f);
        textureSize = new Vector2(1, 1);
    }
    private static final Animation<TextureRegion> ANIMATION_IDLE = TextureUtil.loadAnimation("./textures/orange_robot/idle.png", 4, 2, 5, 0.2f);
    public static final Animation<TextureRegion> ANIMATION_RUN = TextureUtil.loadAnimation("./textures/orange_robot/run.png", 5, 1, 0.15f);
    private static final Animation<TextureRegion> ANIMATION_FLYING = TextureUtil.loadAnimation("./textures/orange_robot/flying.png", 2, 1, 0.2f);
    private static final Animation<TextureRegion> ANIMATION_ATTACK_HORIZONTAL = TextureUtil.loadAnimation("./textures/orange_robot/attack_horizontal.png", 2, 2, 0.1f);
    private static final Animation<TextureRegion> ANIMATION_ATTACK_VERTICAL = TextureUtil.loadAnimation("./textures/orange_robot/attack_vertical.png", 2, 2, 0.1f);
    @Getter
    private boolean characterCollisionsEnabled;

    @Override
    public void createBody() {
        super.createBody();
        characterFixture.getFilterData().categoryBits |= Collisions.PLAYER;
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
                    bounce(player);
                }
                solveCollision = false;
            }
            contact.setEnabled(solveCollision);
        }
    }

    private void bounce(Player otherPlayer) {
        float bounceStrengthX = 0.5f + Math.min(Math.max(Math.abs(body.getLinearVelocity().x), Math.abs(otherPlayer.getBody().getLinearVelocity().x)) / 3, 1);
        float bounceStrengthY = 1;
        Vector2 directionToTarget = body.getPosition().cpy().sub(otherPlayer.getBody().getPosition()).nor();
        Vector2 impulse = directionToTarget.cpy().scl(bounceStrengthX, bounceStrengthY);
        body.setLinearVelocity(new Vector2());
        body.applyLinearImpulse(impulse, body.getPosition(), true);
        otherPlayer.setOneTimeAnimation((Math.abs(impulse.x) > Math.abs(impulse.y)) ? ANIMATION_ATTACK_HORIZONTAL : ANIMATION_ATTACK_VERTICAL);
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
        if (walkDirection != 0) {
            return ANIMATION_RUN;
        }
        return ANIMATION_IDLE;
    }
}
