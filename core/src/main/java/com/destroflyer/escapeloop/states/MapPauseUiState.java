package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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

        TextButton continueButton = new TextButton("Continue", skin);
        continueButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapState.closePauseMenu();
            }
        });
        menuTable.add(continueButton).fill();

        menuTable.row().padTop(10);

        playerPastTrajectoryDurationLabel = new Label("", skin);
        updatePlayerPastTrajectoryDurationLabel();
        menuTable.add(playerPastTrajectoryDurationLabel);

        menuTable.row().padTop(10);

        Slider playerPastTrajectoryDurationSlider = new Slider(0, 6, 0.1f, false, skin);
        playerPastTrajectoryDurationSlider.setValue(mapState.getMapRenderState().getPlayerPastTrajectoryDuration());
        playerPastTrajectoryDurationSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mapState.getMapRenderState().setPlayerPastTrajectoryDuration(playerPastTrajectoryDurationSlider.getValue());
                updatePlayerPastTrajectoryDurationLabel();
            }
        });
        menuTable.add(playerPastTrajectoryDurationSlider).width(200).center();

        menuTable.row().padTop(10);

        TextButton exitButton = new TextButton("Back to level selection", skin);
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

    private void updatePlayerPastTrajectoryDurationLabel() {
        playerPastTrajectoryDurationLabel.setText("Player past trajectory duration: " + FloatUtil.format(mapState.getMapRenderState().getPlayerPastTrajectoryDuration(), 1) + "s");
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
