package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MapPauseState extends UiState {

    public MapPauseState(MapState<?, ?> mapState) {
        this.mapState = mapState;
        hasBackdrop = true;
    }
    private MapState<?, ?> mapState;

    @Override
    public void create() {
        super.create();
        Table menuTable = new Table();

        // Continue

        TextButton continueButton = new TextButton("Continue", main.getSkinLarge());
        continueButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
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
                playButtonSound();
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
                playButtonSound();
            }
        });
        menuTable.add(exitButton).fill();

        menuTable.setFillParent(true);
        menuTable.center();

        stage.addActor(menuTable);
    }

    private void close() {
        mapState.closePauseMenu();
        playButtonSound();
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    close();
                    return true;
                }
                return false;
            }
        };
    }
}
