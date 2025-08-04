package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.inputs.ActionInput;
import com.destroflyer.escapeloop.game.inputs.JumpInput;
import com.destroflyer.escapeloop.game.inputs.SetVerticalDirectionInput;
import com.destroflyer.escapeloop.game.inputs.SetWalkDirectionInput;
import com.destroflyer.escapeloop.util.FloatUtil;

public class MapIngameState extends UiState {

    public MapIngameState(MapState mapState) {
        this.mapState = mapState;
    }
    private MapState mapState;
    private Label infoLabel;
    private Label timeLabel;

    @Override
    public void create() {
        super.create();
        infoLabel = new Label(null, main.getSkinLarge());
        infoLabel.setPosition(20, (Main.VIEWPORT_HEIGHT - 36));
        stage.addActor(infoLabel);

        timeLabel = new Label(null, main.getSkinLarge());
        timeLabel.setPosition((Main.VIEWPORT_WIDTH - 230), (Main.VIEWPORT_HEIGHT - 36));
        stage.addActor(timeLabel);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        Map map = mapState.getMap();

        updateWalkDirection(map);
        updateVerticalDirection(map);
        map.update(tpf);

        int remainingPlayerPasts = map.getMaximumPlayerPasts() - mapState.getMap().getPlayerPasts().size();
        infoLabel.setText("J = Action, K = Retry, L = Time travel (" + remainingPlayerPasts + "/" + map.getMaximumPlayerPasts() +  " charges), Backspace = Reset");
        timeLabel.setText("Time: " + FloatUtil.format(mapState.getMap().getTotalTime(), 3) + "s");
    }

    private void updateWalkDirection(Map map) {
        int walkDirection = mapState.isDirectionLeft() ? -1 : (mapState.isDirectionRight() ? 1 : 0);
        if (walkDirection != map.getPlayer().getWalkDirection()) {
            map.applyInput(new SetWalkDirectionInput(walkDirection));
        }
    }

    private void updateVerticalDirection(Map map) {
        int verticalDirection = mapState.isDirectionDown() ? -1 : (mapState.isDirectionUp() ? 1 : 0);
        if (verticalDirection != map.getPlayer().getVerticalDirection()) {
            map.applyInput(new SetVerticalDirectionInput(verticalDirection));
        }
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                Map map = mapState.getMap();
                switch (keycode) {
                    case Input.Keys.SPACE:
                        map.applyInput(new JumpInput());
                        return true;
                    case Input.Keys.J:
                        map.applyInput(new ActionInput());
                        return true;
                    case Input.Keys.K:
                        map.respawnCurrentPlayer();
                        return true;
                    case Input.Keys.L:
                        map.tryStartNextPlayer();
                        return true;
                    case Input.Keys.BACKSPACE:
                        map.reset();
                        return true;
                    case Input.Keys.ESCAPE:
                        mapState.openPauseMenu();
                        return true;
                }
                return false;
            }
        };
    }
}
