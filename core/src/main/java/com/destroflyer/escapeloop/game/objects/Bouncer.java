package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.destroflyer.escapeloop.game.Collisions;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.util.TextureUtil;

public class Bouncer extends MapObject {

    public Bouncer() {
        textureOffset = new Vector2(0, (2f / 16) * Map.TILE_SIZE);
    }
    private static final TextureRegion TEXTURE_REGION = TextureUtil.loadCaveTextureRegion(6, 6);

    @Override
    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = map.getWorld().createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius((6f / 16) * Map.TILE_SIZE);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.isSensor = true;
        Fixture fixture = body.createFixture(fixtureDef);

        Filter filter = new Filter();
        filter.categoryBits = Collisions.BOUNCER;
        filter.maskBits = Collisions.CHARACTER | Collisions.ITEM;
        fixture.setFilterData(filter);
    }

    @Override
    public void onContactBegin(MapObject mapObject, Fixture ownFixture, Fixture otherFixture, Contact contact) {
        super.onContactBegin(mapObject, ownFixture, otherFixture, contact);
        mapObject.bounceOff(this);
    }

    @Override
    public TextureRegion getCurrentTextureRegion() {
        return TEXTURE_REGION;
    }
}
