package com.destroflyer.escapeloop.game.objects.items;

import com.destroflyer.escapeloop.game.objects.Character;
import com.destroflyer.escapeloop.game.objects.Item;

public class FreezeItem extends Item {

    public FreezeItem() {
        super("freeze");
    }

    @Override
    protected boolean onImpact(Character thrower, Character target) {
        // TODO: Freeze
        return true;
    }
}
