package com.destroflyer.escapeloop.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureUtil {

    public static Animation<TextureRegion> loadAnimation(String path, int cols, int rows, float frameDuration) {
        return loadAnimation(path, cols, rows, cols * rows, frameDuration);
    }

    public static Animation<TextureRegion> loadAnimation(String path, int cols, int rows, int totalFrames, float frameDuration) {
        Texture texture = new Texture(path);
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
}
