package com.destroflyer.escapeloop.game.objects.items;

import com.destroflyer.escapeloop.game.objects.Character;
import com.destroflyer.escapeloop.game.objects.Item;

public class KnockbackItem extends Item {

    public KnockbackItem() {
        super("knockback");
    }

    @Override
    protected boolean onImpact(Character thrower, Character target) {
        target.bounceOff(this);
        bounceOff(target);
        return false;
    }
}
