package com.destroflyer.escapeloop.game.objects.items;

import com.destroflyer.escapeloop.game.Particles;
import com.destroflyer.escapeloop.game.objects.Item;

public class HeavyItem extends Item {

    public HeavyItem() {
        super("heavy");
    }

    @Override
    public Particles getCurrentParticles() {
        return Particles.DOWN;
    }
}
