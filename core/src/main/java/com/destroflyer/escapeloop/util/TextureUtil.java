package com.destroflyer.escapeloop.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.destroflyer.escapeloop.game.Skin;
import com.destroflyer.escapeloop.game.Skins;

import java.util.HashMap;

public class TextureUtil {

    public static final Texture MAP_BACKGROUND_TEXTURE = new Texture("./textures/map/background.png");
    private static final Texture MAP_OBJECTS_TEXTURE = new Texture("./textures/map/objects.png");
    private static final Texture SCIENTIST_TEXTURE = new Texture("./textures/scientist/default.png");
    private static final Texture EYE_ICON_TEXTURE = new Texture("./textures/menu/eye.png");
    private static final HashMap<String, Texture> PLAYER_TEXTURES = new HashMap<>();
    private static final HashMap<String, Texture> ENEMY_TEXTURES = new HashMap<>();

    static {
        for (Skin playerSkin : Skins.PLAYER) {
            PLAYER_TEXTURES.put(playerSkin.getName(), new Texture("./textures/player/" + playerSkin.getName() + ".png"));
        }
        for (Skin enemySkin : Skins.ENEMY) {
            ENEMY_TEXTURES.put(enemySkin.getName(), new Texture("./textures/enemy/" + enemySkin.getName() + ".png"));
        }
    }

    public static Animation<TextureRegion> getPlayerAnimation(Skin skin, int row, int totalFrames, float frameDuration) {
        return getLinearAnimation(getPlayerTexture(skin), 9, 5, row, totalFrames, frameDuration);
    }

    public static Animation<TextureRegion> getEnemyAnimation(Skin skin, int row, int totalFrames, float frameDuration) {
        return getLinearAnimation(getEnemyTexture(skin), 5, 4, row, totalFrames, frameDuration);
    }

    public static Texture getPlayerTexture(Skin skin) {
        return PLAYER_TEXTURES.get(skin.getName());
    }

    public static Texture getEnemyTexture(Skin skin) {
        return ENEMY_TEXTURES.get(skin.getName());
    }

    public static Animation<TextureRegion> getScientistAnimation(int row, int totalFrames, float frameDuration) {
        return getLinearAnimation(SCIENTIST_TEXTURE, 3, 8, row, totalFrames, frameDuration);
    }

    private static Animation<TextureRegion> getLinearAnimation(Texture texture, int rows, int cols, int row, int totalFrames, float frameDuration) {
        int frameWidth = texture.getWidth() / cols;
        int frameHeight = texture.getHeight() / rows;
        TextureRegion[] frames = new TextureRegion[totalFrames];
        int frameIndex = 0;
        for (int col = 0; col < cols; col++) {
            if (frameIndex >= totalFrames) {
                break;
            }
            frames[frameIndex++] = new TextureRegion(texture, col * frameWidth, row * frameHeight, frameWidth, frameHeight);
        }
        return new Animation<>(frameDuration, frames);
    }

    public static TextureRegion getMapObjectsTextureRegion(int x, int y) {
        return getMapObjectsTextureRegion(x, y, 1, 1);
    }

    public static TextureRegion getMapObjectsTextureRegion(int x, int y, int width, int height) {
        int tileSize = 16;
        return new TextureRegion(MAP_OBJECTS_TEXTURE, x * tileSize, y * tileSize, width * tileSize, height * tileSize);
    }

    public static TextureRegion getEyeIconTextureRegion(int x) {
        int tileSize = 15;
        return new TextureRegion(EYE_ICON_TEXTURE, x * tileSize, 0, tileSize, tileSize);
    }
}
