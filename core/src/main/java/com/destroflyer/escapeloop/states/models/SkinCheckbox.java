package com.destroflyer.escapeloop.states.models;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SkinCheckbox {
    private CheckBox checkBox;
    private Animation<TextureRegion> animation;
    private TextureRegionDrawable animationDrawable;
    private int requiredMapRecords;
}
