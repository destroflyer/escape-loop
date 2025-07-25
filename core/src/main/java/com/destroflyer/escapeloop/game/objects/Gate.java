package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.destroflyer.escapeloop.game.Collisions;
import com.destroflyer.escapeloop.game.MapObject;

import lombok.Getter;

public class Gate extends MapObject {

    public Gate(float width, float height) {
        this.width = width;
        this.height = height;
    }
    public static final TextureRegion TEXTURE_REGION = new TextureRegion(new Texture("./textures/gate.png"));
    @Getter
    private float width;
    @Getter
    private float height;

    @Override
    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = map.getWorld().createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox((width / 2), (height / 2));
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        Fixture fixture = body.createFixture(fixtureDef);

        Filter filter = new Filter();
        filter.categoryBits = Collisions.GATE;
        filter.maskBits = Collisions.CHARACTER;
        fixture.setFilterData(filter);
    }

    @Override
    public TextureRegion getCurrentTextureRegion() {
        return TEXTURE_REGION;
    }
}
