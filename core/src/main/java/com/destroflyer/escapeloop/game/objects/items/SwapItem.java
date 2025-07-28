package com.destroflyer.escapeloop.game.objects.items;

import com.badlogic.gdx.math.Vector2;
import com.destroflyer.escapeloop.game.objects.Character;
import com.destroflyer.escapeloop.game.objects.Item;

public class SwapItem extends Item {

    public SwapItem() {
        super("swap");
    }

    @Override
    protected boolean onImpact(Character thrower, Character target) {
        Vector2 holderPosition = thrower.getBody().getPosition().cpy();
        Vector2 targetPosition = target.getBody().getPosition().cpy();
        map.queueTask(() -> {
            thrower.getBody().setTransform(targetPosition, thrower.getBody().getAngle());
            target.getBody().setTransform(holderPosition, target.getBody().getAngle());
        });
        bounceOff(target);
        return false;
    }
}
