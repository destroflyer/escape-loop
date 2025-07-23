package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MapUiState extends UiState {

    @Override
    protected void create(Skin skin, float width, float height) {
        Label info = new Label("Enter = Time machine, Backspace = Reset, F1 = Debug, Escape = Back to main menu", skin);
        info.setPosition(15, (height - 50));
        stage.addActor(info);
    }
}
