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

import lombok.Getter;

public class PressureTrigger extends MapObject {

    public PressureTrigger(ArrayList<Gate> gates) {
        this.gates = gates;
        textureOffset = new Vector2(0, (((16 - 4) / 2f) / 16) * Map.TILE_SIZE);
    }
    private static final TextureRegion TEXTURE_REGION_UP = TextureUtil.getMapObjectsTextureRegion(0, 1);
    private static final TextureRegion TEXTURE_REGION_DOWN = TextureUtil.getMapObjectsTextureRegion(1, 1);
    @Getter
    private ArrayList<Gate> gates;
    @Getter
    private boolean state;

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
        filter.categoryBits = Collisions.PRESSURE_TRIGGER;
        filter.maskBits = Collisions.CHARACTER | Collisions.ITEM;
        fixture.setFilterData(filter);
    }

    @Override
    public void onContactBegin(MapObject mapObject, Fixture ownFixture, Fixture otherFixture) {
        super.onContactBegin(mapObject, ownFixture, otherFixture);
        updateState();
    }

    @Override
    public void onContactEnd(MapObject mapObject, Fixture ownFixture, Fixture otherFixture) {
        super.onContactEnd(mapObject, ownFixture, otherFixture);
        updateState();
    }

    private void updateState() {
        boolean newState = activeContacts.size() > 0;
        if (newState != state) {
            state = newState;
            for (Gate gate : gates) {
                boolean isGateOpening = map.getObjects().stream().anyMatch(mapObject -> {
                    if (mapObject instanceof PressureTrigger) {
                        PressureTrigger pressureTrigger = (PressureTrigger) mapObject;
                        return pressureTrigger.isState() && pressureTrigger.getGates().contains(gate);
                    }
                    return false;
                });
                gate.setOpening(isGateOpening);
            }
            map.getAudioState().playSound("trigger");
        }
    }

    @Override
    public TextureRegion getSimpleTextureRegion() {
        return state ? TEXTURE_REGION_DOWN : TEXTURE_REGION_UP;
    }
}
