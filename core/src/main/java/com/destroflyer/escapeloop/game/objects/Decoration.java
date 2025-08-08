package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.util.TextureUtil;

public class Decoration extends MapObject {

    public Decoration(int textureRegionX, int textureRegionY) {
        textureRegion = TextureUtil.loadLabDecorationsTextureRegion(textureRegionX, textureRegionY, 1, 1);
    }
    private TextureRegion textureRegion;

    @Override
    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = map.getWorld().createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        float width = Map.TILE_SIZE;
        float height = Map.TILE_SIZE;
        polygonShape.setAsBox(width / 2, height / 2);
    }

    @Override
    public TextureRegion getTextureRegion() {
        return textureRegion;
    }
}
