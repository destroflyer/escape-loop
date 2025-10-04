package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
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
    }

    @Override
    public TextureRegion getSimpleTextureRegion() {
        return textureRegion;
    }
}
