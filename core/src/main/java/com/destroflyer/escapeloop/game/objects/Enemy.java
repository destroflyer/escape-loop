package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.destroflyer.escapeloop.util.TextureUtil;

public class Enemy extends Character {

    public Enemy(float shootCooldown, boolean autoShoot) {
        this.shootCooldown = shootCooldown;
        remainingShootCooldown = shootCooldown;
        this.autoShoot = autoShoot;
        textureSize = new Vector2(1, 1);
    }
    private static final Animation<TextureRegion> ANIMATION_IDLE = TextureUtil.loadAnimation("./textures/enemy_robot/idle.png", 2, 2, 0.2f);
    private static final Animation<TextureRegion> ANIMATION_RUN = TextureUtil.loadAnimation("./textures/enemy_robot/run.png", 2, 2, 0.15f);
    private static final Animation<TextureRegion> ANIMATION_SHOOT = TextureUtil.loadAnimation("./textures/enemy_robot/shoot.png", 2, 1, 0.05f);
    private float shootCooldown;
    private float remainingShootCooldown;
    private boolean autoShoot;

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (remainingShootCooldown > 0) {
            remainingShootCooldown -= tpf;
            if (autoShoot) {
                tryShoot();
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
