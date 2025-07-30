package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.destroflyer.escapeloop.game.Collisions;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.util.TextureUtil;

import java.util.concurrent.atomic.AtomicReference;

public class Enemy extends Character {

    public Enemy(int hoverTileHeight, float shootCooldown, boolean autoShoot) {
        this.hoverTileHeight = hoverTileHeight;
        this.shootCooldown = shootCooldown;
        remainingShootCooldown = shootCooldown;
        this.autoShoot = autoShoot;
        textureSize = new Vector2(1, 1);
    }
    private static final Animation<TextureRegion> ANIMATION_IDLE = TextureUtil.loadAnimation("./textures/enemy_robot/idle.png", 2, 2, 0.2f);
    private static final Animation<TextureRegion> ANIMATION_RUN = TextureUtil.loadAnimation("./textures/enemy_robot/run.png", 2, 2, 0.15f);
    private static final Animation<TextureRegion> ANIMATION_SHOOT = TextureUtil.loadAnimation("./textures/enemy_robot/shoot.png", 2, 1, 0.05f);
    private static final float HOVER_SPRING_STRENGTH = 50;
    private static final float HOVER_DAMPING = 5;
    private int hoverTileHeight;
    private float shootCooldown;
    private float remainingShootCooldown;
    private boolean autoShoot;

    @Override
    public void update(float tpf) {
        super.update(tpf);
        handleHover();
        if (remainingShootCooldown > 0) {
            remainingShootCooldown -= tpf;
            if (autoShoot) {
                tryShoot();
            }
        }
    }

    private void handleHover() {
        if (hoverTileHeight > 0) {
            float effectiveHoverHeight = RADIUS + (((int) (hoverTileHeight / body.getGravityScale()) * Map.TILE_SIZE));
            AtomicReference<Vector2> groundPoint = new AtomicReference<>();
            map.getWorld().rayCast((fixture, point, normal, fraction) -> {
                if (Collisions.hasCategory(fixture, Collisions.PLATFORM)) {
                    groundPoint.set(point);
                    return fraction;
                }
                return -1;
            }, body.getPosition(), body.getPosition().cpy().sub(0, effectiveHoverHeight));

            if (groundPoint.get() != null) {
                float heightAboveGround = body.getPosition().y - groundPoint.get().y;
                float heightError = effectiveHoverHeight - heightAboveGround;
                float springForce = (HOVER_SPRING_STRENGTH * heightError) - (HOVER_DAMPING * body.getLinearVelocity().y);
                body.applyForceToCenter(0, springForce, true);
            }
        }
    }

    private void tryShoot() {
        if (remainingShootCooldown <= 0) {
            map.queueTask(() -> {
                Bullet bullet = new Bullet(this);
                map.addObject(bullet);
                bullet.getBody().setTransform(body.getPosition(), 0);
                bullet.getBody().setLinearVelocity(new Vector2(viewDirection * 2, 0));
            });
            remainingShootCooldown = shootCooldown;
            setOneTimeAnimation(ANIMATION_SHOOT);
        }
    }

    @Override
    protected Animation<TextureRegion> getLoopedAnimation() {
        return (walkDirection != 0) ? ANIMATION_RUN : ANIMATION_IDLE;
    }
}
