package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.destroflyer.escapeloop.game.Collisions;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.game.Particles;
import com.destroflyer.escapeloop.util.TextureUtil;

public class Bouncer extends MapObject {

    public Bouncer() {
        textureOffset = new Vector2(0, (((16 - 4) / 2f) / 16) * Map.TILE_SIZE);
    }
    private static final TextureRegion TEXTURE_REGION = TextureUtil.getMapObjectsTextureRegion(0, 2);

    @Override
    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = map.getWorld().createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        float width = Map.TILE_SIZE;
        float height = Map.TILE_SIZE * (4f / 16);
        polygonShape.setAsBox(width / 2, height / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;
        Fixture fixture = body.createFixture(fixtureDef);
        polygonShape.dispose();

        Filter filter = new Filter();
        filter.categoryBits = Collisions.BOUNCER;
        filter.maskBits = Collisions.CHARACTER | Collisions.ITEM;
        fixture.setFilterData(filter);
    }

    @Override
    public void onContactBegin(MapObject mapObject, Fixture ownFixture, Fixture otherFixture) {
        super.onContactBegin(mapObject, ownFixture, otherFixture);
        mapObject.bounceOff(this);
    }

    @Override
    public TextureRegion getSimpleTextureRegion() {
        return TEXTURE_REGION;
    }

    @Override
    public Particles getParticles() {
        return Particles.CIRCLE;
    }
}
