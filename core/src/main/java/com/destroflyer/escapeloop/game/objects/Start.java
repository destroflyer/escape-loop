package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.util.TextureUtil;

public class Start extends MapObject {

    public Start() {
        textureOffset = new Vector2(0, 0.125f);
        textureSize = new Vector2(0.75f, 0.75f);
    }
    private static final TextureRegion TEXTURE_REGION = TextureUtil.loadLabDecorationsTextureRegion(1, 3, 2, 2);

    @Override
    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = map.getWorld().createBody(bodyDef);
    }

    @Override
    public TextureRegion getTextureRegion() {
        return TEXTURE_REGION;
    }
}
