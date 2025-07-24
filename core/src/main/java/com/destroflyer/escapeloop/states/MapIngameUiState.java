package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.destroflyer.escapeloop.Main;

public class MapIngameUiState extends UiState {

    @Override
    protected void create(Skin skin) {
        Label infoLabel = new Label("Enter = Time machine, Backspace = Reset, F1 = Debug, Escape = Menu", skin);
        infoLabel.setPosition(20, (Main.VIEWPORT_HEIGHT - 55));
        stage.addActor(infoLabel);
    }
}
