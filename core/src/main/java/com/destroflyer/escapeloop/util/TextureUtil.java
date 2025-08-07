package com.destroflyer.escapeloop.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureUtil {

    private static final Texture CAVE_TEXTURE = new Texture("./textures/cave/main.png");
    private static final Texture LAB_MAIN_TEXTURE = new Texture("./textures/lab/main.png");
    private static final Texture LAB_DECORATIONS_TEXTURE = new Texture("./textures/lab/decorations.png");
    private static final Texture SCIENTISTS_TEXTURE = new Texture("./textures/scientists/scientists.png");

    public static Animation<TextureRegion> loadWrappedAnimation(String path, int cols, int rows, float frameDuration) {
        return loadWrappedAnimation(path, cols, rows, cols * rows, frameDuration);
    }

    public static Animation<TextureRegion> loadWrappedAnimation(String path, int cols, int rows, int totalFrames, float frameDuration) {
        return loadWrappedAnimation(new Texture(path), cols, rows, totalFrames, frameDuration);
    }

    public static Animation<TextureRegion> loadWrappedAnimation(Texture texture, int cols, int rows, int totalFrames, float frameDuration) {
        int frameWidth = texture.getWidth() / cols;
        int frameHeight = texture.getHeight() / rows;
        TextureRegion[] frames = new TextureRegion[totalFrames];
        int frameIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (frameIndex >= totalFrames) {
                    break;
                }
                frames[frameIndex++] = new TextureRegion(texture, col * frameWidth, row * frameHeight, frameWidth, frameHeight);
            }
        }
        return new Animation<>(frameDuration, frames);
    }

    public static Animation<TextureRegion> loadScientistsAnimation(int row, int totalFrames, float frameDuration) {
        return loadLinearAnimation(SCIENTISTS_TEXTURE, 16, 23, row, totalFrames, frameDuration);
    }

    public static Animation<TextureRegion> loadLinearAnimation(Texture texture, int rows, int cols, int row, int totalFrames, float frameDuration) {
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

    public static TextureRegion loadCaveTextureRegion(int x, int y) {
        return loadTextureRegion(CAVE_TEXTURE, 29, 12, x, y);
    }

    public static TextureRegion loadLabMainTextureRegion(int x, int y, int width, int height) {
        int tileSize = 16;
        return new TextureRegion(LAB_MAIN_TEXTURE, x * tileSize, y * tileSize, width * tileSize, height * tileSize);
    }

    public static TextureRegion loadLabDecorationsTextureRegion(int x, int y, int width, int height) {
        int tileSize = 16;
        return new TextureRegion(LAB_DECORATIONS_TEXTURE, x * tileSize, y * tileSize, width * tileSize, height * tileSize);
    }

    public static TextureRegion loadTextureRegion(Texture texture, int cols, int rows, int x, int y) {
        int frameWidth = texture.getWidth() / cols;
        int frameHeight = texture.getHeight() / rows;
        return new TextureRegion(texture, x * frameWidth, y * frameHeight, frameWidth, frameHeight);
    }
}
