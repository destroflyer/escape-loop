package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.destroflyer.escapeloop.Main;

public class MapPauseState extends UiState {

    public MapPauseState(MapState mapState) {
        this.mapState = mapState;
    }
    private MapState mapState;

    @Override
    public void create() {
        super.create();
        Table menuTable = new Table();

        // Continue

        TextButton continueButton = new TextButton("Continue", main.getSkinLarge());
        continueButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapState.closePauseMenu();
            }
        });
        menuTable.add(continueButton).fill();

        // Settings

        menuTable.row().padTop(10);

        TextButton settingsButton = new TextButton("Settings", main.getSkinLarge());
        settingsButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                stageVisible = false;
                main.openSettings(() -> stageVisible = true);
            }
        });
        menuTable.add(settingsButton).fill();

        // Exit

        menuTable.row().padTop(10);

        TextButton exitButton = new TextButton("Exit", main.getSkinLarge());
        exitButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapState.backToMapSelection();
            }
        });
        menuTable.add(exitButton).fill();

        menuTable.setFillParent(true);
        menuTable.center();

        stage.addActor(menuTable);
    }

    @Override
    public void render() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.75f);
        shapeRenderer.rect(0, 0, Main.VIEWPORT_WIDTH, Main.VIEWPORT_HEIGHT);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        super.render();
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.ESCAPE:
                        mapState.closePauseMenu();
                        return true;
                }
                return false;
            }
        };
    }
}
