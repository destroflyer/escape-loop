package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.State;

import lombok.Getter;

public abstract class UiState extends State {

    @Getter
    protected Stage stage;

    @Override
    public void create() {
        super.create();
        stage = new Stage(main.getViewport());
        create(main.getSkin(), Main.VIEWPORT_WIDTH, Main.VIEWPORT_HEIGHT);
    }

    protected abstract void create(Skin skin, float width, float height);

    public void update(float tpf) {
        stage.act(tpf);
    }

    public void render() {
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }
}
