package com.destroflyer.escapeloop.game.objects.items;

import com.badlogic.gdx.math.Vector2;
import com.destroflyer.escapeloop.game.Particles;
import com.destroflyer.escapeloop.game.objects.Character;
import com.destroflyer.escapeloop.game.objects.Item;

public class HeavyItem extends Item {

    public HeavyItem() {
        super("heavy");
    }
    private static final float BONUS_GRAVITY_SCALE = 1;

    @Override
    protected void onPickup(Character holder) {
        super.onPickup(holder);
        holder.getBody().setGravityScale(1 + BONUS_GRAVITY_SCALE);
    }

    @Override
    protected void onThrow(Vector2 velocity) {
        super.onThrow(velocity);
        thrower.getBody().setGravityScale(1);
    }

    @Override
    public Particles getCurrentParticles() {
        return Particles.DOWN;
    }
}
