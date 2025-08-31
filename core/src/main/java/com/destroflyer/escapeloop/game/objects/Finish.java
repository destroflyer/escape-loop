package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.destroflyer.escapeloop.game.Collisions;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.util.TextureUtil;

public class Finish extends MapObject {

    public Finish() {
        textureOffset = new Vector2(0, 0.125f);
        textureSize = new Vector2(0.75f, 0.75f);
    }
    private static final TextureRegion TEXTURE_REGION = TextureUtil.loadLabDecorationsTextureRegion(1, 1, 2, 2);

    @Override
    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = map.getWorld().createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(Map.TILE_SIZE / 2, Map.TILE_SIZE / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;
        Fixture fixture = body.createFixture(fixtureDef);

        Filter filter = new Filter();
        filter.categoryBits = Collisions.FINISH;
        filter.maskBits = Collisions.PLAYER;
        fixture.setFilterData(filter);
    }

    @Override
    public void onContactBegin(MapObject mapObject, Fixture ownFixture, Fixture otherFixture, Contact contact) {
        super.onContactBegin(mapObject, ownFixture, otherFixture, contact);
        map.onFinish();
    }

    @Override
    public TextureRegion getSimpleTextureRegion() {
        return TEXTURE_REGION;
    }
}
