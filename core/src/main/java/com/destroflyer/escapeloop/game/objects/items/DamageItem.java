package com.destroflyer.escapeloop.game.objects.items;

import com.destroflyer.escapeloop.game.objects.Character;
import com.destroflyer.escapeloop.game.objects.Item;

public class DamageItem extends Item {

    public DamageItem() {
        super("damage");
    }

    @Override
    protected boolean onImpact(Character thrower, Character target) {
        // TODO: Damage
        return false;
    }
}
