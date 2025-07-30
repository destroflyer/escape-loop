package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.destroflyer.escapeloop.game.Collisions;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.util.TextureUtil;

import lombok.Getter;
import lombok.Setter;

public class Gate extends MapObject {

    public Gate(float width, float height) {
        this.width = width;
        this.height = height;
    }
    public static final TextureRegion TEXTURE_REGION = TextureUtil.loadLabMainTextureRegion(0, 16, 1, 1);
    private static final float OPEN_SPEED_PER_SIZE = 2;
    @Getter
    private float width;
    @Getter
    private float height;
    private float lastOpenProgress;
    @Getter
    private float openProgress;
    @Getter
    @Setter
    private boolean opening;
    private Fixture fixture;

    @Override
    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = map.getWorld().createBody(bodyDef);
        updateBody();
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        float openSpeed = OPEN_SPEED_PER_SIZE / Math.max(width, height);
        openProgress = Math.max(0, Math.min(openProgress + ((opening ? 1 : -1) * openSpeed * tpf), 1));
        if (openProgress != lastOpenProgress) {
            updateBody();
            lastOpenProgress = openProgress;
        }
    }

    private void updateBody() {
        if (fixture != null) {
            body.destroyFixture(fixture);
        }
        float shapeWidth = width;
        float shapeHeight = height;
        float closeProgress = 1 - openProgress;
        if (width > height) {
            shapeWidth *= closeProgress;
        } else {
            shapeHeight *= closeProgress;
        }
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(shapeWidth / 2, shapeHeight / 2, new Vector2(0, (height / 2) - (shapeHeight / 2)), 0);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixture = body.createFixture(fixtureDef);

        Filter filter = new Filter();
        filter.categoryBits = Collisions.PLATFORM;
        filter.maskBits = Collisions.CHARACTER | Collisions.CHARACTER_FOOT_SENSOR | Collisions.ITEM | Collisions.BULLET;
        fixture.setFilterData(filter);

        body.setActive((shapeWidth * shapeHeight) > 0);
    }

    @Override
    public TextureRegion getTextureRegion() {
        return TEXTURE_REGION;
    }
}
