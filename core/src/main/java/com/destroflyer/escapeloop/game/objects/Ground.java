package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.destroflyer.escapeloop.game.Collisions;
import com.destroflyer.escapeloop.game.MapObject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Ground extends MapObject {

    private BodyDef.BodyType bodyType;
    @Getter
    private float width;
    @Getter
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
        filter.categoryBits = Collisions.GROUND;
        filter.maskBits = Collisions.CHARACTER | Collisions.CHARACTER_FOOT_SENSOR | Collisions.ITEM | Collisions.BULLET;
        fixture.setFilterData(filter);
    }
}
