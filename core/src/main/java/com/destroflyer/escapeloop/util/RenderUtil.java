package com.destroflyer.escapeloop.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

public class RenderUtil {

    public static void drawCenteredText(SpriteBatch spriteBatch, GlyphLayout layout, BitmapFont font, int x, int y, String text, Color color, int lineWidth) {
        drawCenteredText(spriteBatch, layout, font, x, y, text, color, lineWidth, null, null, null);
    }

    public static void drawCenteredText(SpriteBatch spriteBatch, GlyphLayout layout, BitmapFont font, int x, int y, String text, Color color, int lineWidth, ShapeRenderer shapeRenderer, Color backdropColor, Float backdropPadding) {
        layout.setText(font, text, color, lineWidth, Align.center, true);
        if (backdropColor != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(backdropColor);
            shapeRenderer.rect(x - ((layout.width / 2f) + backdropPadding), y - ((layout.height / 2) + backdropPadding), layout.width + (2 * backdropPadding), layout.height + (2 * backdropPadding));
            shapeRenderer.end();
        }
        spriteBatch.begin();
        font.draw(spriteBatch, layout, x - (lineWidth / 2f), y + (layout.height / 2));
        spriteBatch.end();
    }
}
