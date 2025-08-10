package com.destroflyer.escapeloop.game.cinematics;

import com.badlogic.gdx.graphics.Texture;

import lombok.Getter;

@Getter
public class IntroCinematicSection {

    public IntroCinematicSection(String text, String textureName) {
        this.text = text;
        texture = new Texture("textures/intro/" + textureName + ".png");
    }
    private String text;
    private Texture texture;
}
