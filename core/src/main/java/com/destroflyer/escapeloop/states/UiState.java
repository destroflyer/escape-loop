package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.State;

import lombok.Getter;

public abstract class UiState extends State {

    @Getter
    protected Stage stage;
    protected boolean stageVisible = true;
    protected boolean hasBackdrop;

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
        if (hasBackdrop) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.75f);
            shapeRenderer.rect(0, 0, Main.VIEWPORT_WIDTH, Main.VIEWPORT_HEIGHT);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
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
