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
    private Label timeLabel;

    @Override
    protected void create(Skin skin) {
        Label infoLabel = new Label("Enter = Time machine, J = Action, Backspace = Reset, Escape = Menu", skin);
        infoLabel.setPosition(20, (Main.VIEWPORT_HEIGHT - 55));
        stage.addActor(infoLabel);

        timeLabel = new Label("", skin);
        timeLabel.setPosition((Main.VIEWPORT_WIDTH - 230), (Main.VIEWPORT_HEIGHT - 36));
        stage.addActor(timeLabel);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        updateTimeLabel();
    }

    private void updateTimeLabel() {
        timeLabel.setText("Time: " + FloatUtil.format(mapState.getMap().getTime(), 3) + "s");
    }
}
