package com.destroflyer.escapeloop.game.behaviours;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.destroflyer.escapeloop.game.Behaviour;
import com.destroflyer.escapeloop.game.MapObject;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class VerticalMovementBehaviour extends Behaviour<MapObject> {

    private float minY;
    private float maxY;
    private float velocity;

    @Override
    public void update(float tpf) {
        Body body = mapObject.getBody();
        if (body.getPosition().y <= minY) {
            body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, velocity));
        } else if (body.getPosition().y >= maxY) {
            body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, -1 * velocity));
        }
    }
}
