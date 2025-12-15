package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.destroflyer.escapeloop.util.TimeUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MapFinishedState extends UiState {

    private int mapIndex;
    private int totalFrames;

    @Override
    public void create() {
        super.create();
        Table menuTable = new Table();

        Label mapLabel = new Label("Level " + (mapIndex + 1), main.getSkinLarge());
        menuTable.add(mapLabel);

        menuTable.row().padTop(2);

        Label timeLabel = new Label(TimeUtil.formatFrames(totalFrames), main.getSkinLarge());
        menuTable.add(timeLabel);

        menuTable.row().padTop(2);

        Label playAgainInfoLabel = new Label("(Press Enter to play again)", main.getSkinSmall());
        menuTable.add(playAgainInfoLabel);

        menuTable.row().padTop(8);

        TextButton playAgainButton = new TextButton("Play again", main.getSkinLarge());
        playAgainButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                playAgain();
            }
        });
        menuTable.add(playAgainButton).fill();

        menuTable.row().padTop(10);

        TextButton exitButton = new TextButton("Exit", main.getSkinLarge());
        exitButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                backToMapSelection();
            }
        });
        menuTable.add(exitButton).fill();

        menuTable.setFillParent(true);
        menuTable.center();

        stage.addActor(menuTable);
    }

    private void playAgain() {
        switchToState(new MapState(mapIndex));
        playButtonSound();
    }

    private void backToMapSelection() {
        switchToState(main.getMapSelectionState());
        playButtonSound();
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    playAgain();
                    return true;
                } else if (keycode == Input.Keys.ESCAPE) {
                    backToMapSelection();
                    return true;
                }
                return false;
            }
        };
    }
}
