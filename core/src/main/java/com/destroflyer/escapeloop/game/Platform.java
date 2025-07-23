package com.destroflyer.escapeloop.game;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Platform extends MapObject {

    private BodyDef.BodyType bodyType;
    private float width;
    private float height;

    @Override
    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        body = map.getWorld().createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, height / 2);
        Fixture fixture = body.createFixture(polygonShape, 0);

        Filter filter = new Filter();
        filter.categoryBits = Collisions.PLATFORM;
        filter.maskBits = Collisions.CHARACTER | Collisions.CHARACTER_FOOT_SENSOR;
        fixture.setFilterData(filter);
    }
}
