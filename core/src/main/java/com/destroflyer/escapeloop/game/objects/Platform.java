package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.destroflyer.escapeloop.util.TextureUtil;

public class Platform extends Ground {

    public Platform(BodyDef.BodyType bodyType, float width, float height) {
        super(bodyType, width, height);
    }
    private static final TextureRegion TEXTURE_REGION_LEFT = TextureUtil.getMapObjectsTextureRegion(0, 0);
    private static final TextureRegion TEXTURE_REGION_CENTER = TextureUtil.getMapObjectsTextureRegion(1, 0);
    private static final TextureRegion TEXTURE_REGION_RIGHT = TextureUtil.getMapObjectsTextureRegion(2, 0);

    @Override
    public TextureRegion getTextureRegion(int tileX, int tileY, int tilesX, int tilesY) {
        if (tileX <= 0) {
            return TEXTURE_REGION_LEFT;
        } else if (tileX >= (tilesX - 1)) {
            return TEXTURE_REGION_RIGHT;
        }
        return TEXTURE_REGION_CENTER;
    }
}
