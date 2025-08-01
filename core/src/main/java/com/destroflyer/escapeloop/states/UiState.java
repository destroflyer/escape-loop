package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.destroflyer.escapeloop.State;

import lombok.Getter;

public abstract class UiState extends State {

    @Getter
    protected Stage stage;
    protected boolean stageVisible = true;

    @Override
    public void create() {
        super.create();
        stage = new Stage(main.getViewport());
        create(main.getSkin());
        inputProcessors.add(stage);
    }

    protected abstract void create(Skin skin);

    public void update(float tpf) {
        stage.act(tpf);
    }

    public void render() {
        if (stageVisible) {
            stage.draw();
        }
    }

    public void dispose() {
        stage.dispose();
    }
}
