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
import com.destroflyer.escapeloop.util.TextureUtil;

import java.util.ArrayList;

public class ToggleTrigger extends MapObject {

    public ToggleTrigger(ArrayList<Gate> gates) {
        this.gates = gates;
        textureOffset = new Vector2(0, (((16 - 7) / 2f) / 16) * Map.TILE_SIZE);
    }
    private static final TextureRegion TEXTURE_REGION_LEFT = TextureUtil.loadCaveTextureRegion(5, 7);
    private static final TextureRegion TEXTURE_REGION_RIGHT = TextureUtil.loadCaveTextureRegion(5, 8);
    private ArrayList<Gate> gates;
    private boolean state;

    @Override
    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = map.getWorld().createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        float width = Map.TILE_SIZE;
        float height = Map.TILE_SIZE * (7f / 16);
        polygonShape.setAsBox(width / 2, height / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;
        Fixture fixture = body.createFixture(fixtureDef);
        polygonShape.dispose();

        Filter filter = new Filter();
        filter.categoryBits = Collisions.TOGGLE_TRIGGER;
        filter.maskBits = Collisions.PLAYER | Collisions.ITEM | Collisions.BULLET;
        fixture.setFilterData(filter);
    }

    public void toggle() {
        state = !state;
        for (Gate gate : gates) {
            gate.setOpening(!gate.isOpening());
        }
        map.getAudioState().playSound("trigger");
    }

    @Override
    public TextureRegion getSimpleTextureRegion() {
        return state ? TEXTURE_REGION_RIGHT : TEXTURE_REGION_LEFT;
    }
}
