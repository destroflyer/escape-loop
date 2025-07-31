package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.util.FloatUtil;

public class MapIngameUiState extends UiState {

    public MapIngameUiState(MapState mapState) {
        this.mapState = mapState;
    }
    private MapState mapState;
    private Label infoLabel;
    private Label timeLabel;

    @Override
    protected void create(Skin skin) {
        infoLabel = new Label("", skin);
        infoLabel.setPosition(20, (Main.VIEWPORT_HEIGHT - 36));
        stage.addActor(infoLabel);

        timeLabel = new Label("", skin);
        timeLabel.setPosition((Main.VIEWPORT_WIDTH - 230), (Main.VIEWPORT_HEIGHT - 36));
        stage.addActor(timeLabel);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        int maximumPlayerPasts = mapState.getMap().getMaximumPlayerPasts();
        int remainingPlayerPasts = maximumPlayerPasts - mapState.getMap().getPlayerPasts().size();
        infoLabel.setText("L = Time machine (" + remainingPlayerPasts + "/" + maximumPlayerPasts +  " charges left), J = Action, Backspace = Reset");
        timeLabel.setText("Time: " + FloatUtil.format(mapState.getMap().getTime(), 3) + "s");
    }
}
