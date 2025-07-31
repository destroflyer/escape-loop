package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.util.FloatUtil;

public class MapPauseUiState extends UiState {

    public MapPauseUiState(MapState mapState) {
        this.mapState = mapState;
    }
    private MapState mapState;
    private Label playerPastTrajectoryDurationLabel;

    @Override
    protected void create(Skin skin) {
        Table menuTable = new Table();

        // Continue

        TextButton continueButton = new TextButton("Continue", skin);
        continueButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapState.closePauseMenu();
            }
        });
        menuTable.add(continueButton).colspan(2).fill();

        // Player pasts - Trajectory duration

        menuTable.row().padTop(10);

        playerPastTrajectoryDurationLabel = new Label(null, skin);
        updatePlayerPastTrajectoryDurationLabel();
        menuTable.add(playerPastTrajectoryDurationLabel);

        Slider playerPastTrajectoryDurationSlider = new Slider(0, 6, 0.1f, false, skin);
        playerPastTrajectoryDurationSlider.setValue(mapState.getMapRenderState().getPlayerPastsTrajectoryDuration());
        playerPastTrajectoryDurationSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mapState.getMapRenderState().setPlayerPastsTrajectoryDuration(playerPastTrajectoryDurationSlider.getValue());
                updatePlayerPastTrajectoryDurationLabel();
            }
        });
        menuTable.add(playerPastTrajectoryDurationSlider).padLeft(10);

        // Player pasts - Distinct colors

        menuTable.row().padTop(10);

        Label playerPastDistinctColorsLabel = new Label("Player pasts - Distinct colors", skin);
        menuTable.add(playerPastDistinctColorsLabel);

        CheckBox playerPastDistinctColorsCheckbox = new CheckBox(null, skin);
        playerPastDistinctColorsCheckbox.setChecked(mapState.getMapRenderState().isPlayerPastsDistinctColors());
        playerPastDistinctColorsCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mapState.getMapRenderState().setPlayerPastsDistinctColors(playerPastDistinctColorsCheckbox.isChecked());
                updatePlayerPastTrajectoryDurationLabel();
            }
        });
        menuTable.add(playerPastDistinctColorsCheckbox).padLeft(10);

        // Exit

        menuTable.row().padTop(10);

        TextButton exitButton = new TextButton("Back to level selection", skin);
        exitButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapState.backToMapSelection();
            }
        });
        menuTable.add(exitButton).colspan(2).fill();

        menuTable.setFillParent(true);
        menuTable.center();

        stage.addActor(menuTable);
    }

    private void updatePlayerPastTrajectoryDurationLabel() {
        playerPastTrajectoryDurationLabel.setText("Player pasts - Trajectory duration: " + FloatUtil.format(mapState.getMapRenderState().getPlayerPastsTrajectoryDuration(), 1) + "s");
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
}
