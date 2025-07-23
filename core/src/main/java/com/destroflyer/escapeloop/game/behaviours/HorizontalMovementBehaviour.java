package com.destroflyer.escapeloop.game.behaviours;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.destroflyer.escapeloop.game.Behaviour;
import com.destroflyer.escapeloop.game.MapObject;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HorizontalMovementBehaviour extends Behaviour<MapObject> {

    private float minX;
    private float maxX;
    private float velocity;

    @Override
    public void update(float tpf) {
        Body body = mapObject.getBody();
        if (body.getPosition().x <= minX) {
            body.setLinearVelocity(new Vector2(velocity, body.getLinearVelocity().y));
        } else if (body.getPosition().x >= maxX) {
            body.setLinearVelocity(new Vector2(-1 * velocity, body.getLinearVelocity().y));
        }
    }
}
