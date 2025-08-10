package com.destroflyer.escapeloop.game.cinematics;

import com.badlogic.gdx.graphics.Texture;

import lombok.Getter;

@Getter
public class IntroCinematicSection {

    public IntroCinematicSection(String textureName, String text) {
        this(textureName, text, 3, 120);
    }

    public IntroCinematicSection(String textureName, String text, float textScale, int textY) {
        texture = new Texture("textures/intro/" + textureName + ".png");
        this.text = text;
        this.textY = textY;
        this.textScale = textScale;
    }
    private Texture texture;
    private String text;
    private float textScale;
    private int textY;
}
