package com.destroflyer.escapeloop.game.objects.items;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.destroflyer.escapeloop.game.Collisions;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.game.objects.Character;
import com.destroflyer.escapeloop.game.objects.Item;
import com.destroflyer.escapeloop.game.objects.ToggleTrigger;

public class KnockbackItem extends Item {

    public KnockbackItem() {
        super("knockback");
    }

    @Override
    public void createBody() {
        super.createBody();
        fixture.getFilterData().maskBits |= Collisions.TOGGLE_TRIGGER;
    }

    @Override
    public void onContactBegin(MapObject mapObject, Fixture ownFixture, Fixture otherFixture) {
        super.onContactBegin(mapObject, ownFixture, otherFixture);
        if (mapObject instanceof ToggleTrigger) {
            ToggleTrigger toggleTrigger = (ToggleTrigger) mapObject;
            toggleTrigger.toggle();
            bounceOff(toggleTrigger);
            resetThrower();
        }
    }

    @Override
    protected boolean onImpact(Character thrower, Character target) {
        target.bounceOff(this);
        bounceOff(target);
        return false;
    }
}
