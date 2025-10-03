package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.scenes.scene2d.Stage;
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
        inputProcessors.add(stage);
    }

    public void update(float tpf) {
        stage.act(tpf);
    }

    public void render() {
        if (stageVisible) {
            stage.draw();
        }
    }

    protected void playButtonSound() {
        if (main.getSettingsState().getPreferences().getBoolean("playSoundMenuButton")) {
            main.getAudioState().playSound("button");
        }
    }

    public void dispose() {
        stage.dispose();
    }
}
