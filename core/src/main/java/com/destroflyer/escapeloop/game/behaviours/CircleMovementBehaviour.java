package com.destroflyer.escapeloop.game.behaviours;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.destroflyer.escapeloop.game.Behaviour;
import com.destroflyer.escapeloop.game.MapObject;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CircleMovementBehaviour extends Behaviour<MapObject> {

    public CircleMovementBehaviour(Vector2 center, float radius, float speed) {
        this(center, radius, speed, 0);
    }
    private Vector2 center;
    private float radius;
    private float speed ;
    private float currentAngle;

    @Override
    public void update(float tpf) {
        Body body = mapObject.getBody();
        currentAngle += speed * tpf;
        float x = (float) (center.x + (Math.cos(currentAngle) * radius));
        float y = (float) (center.y + (Math.sin(currentAngle) * radius));
        Vector2 velocity = new Vector2(x, y).sub(body.getPosition()).scl(1 / tpf);
        body.setLinearVelocity(velocity);
    }
}
