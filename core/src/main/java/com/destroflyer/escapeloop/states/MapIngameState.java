package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.State;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.inputs.ActionInput;
import com.destroflyer.escapeloop.game.inputs.JumpInput;
import com.destroflyer.escapeloop.game.inputs.SetVerticalDirectionInput;
import com.destroflyer.escapeloop.game.inputs.SetWalkDirectionInput;

public class MapIngameState extends State {

    public MapIngameState(MapState mapState) {
        this.mapState = mapState;
    }
    private MapState mapState;
    private MapIngameUiState mapIngameUiState;

    @Override
    public void create() {
        super.create();
        mapIngameUiState = new MapIngameUiState();
        childStates.add(mapIngameUiState);
    }

    @Override
    public void onAdd(Main main) {
        super.onAdd(main);
        main.addState(mapIngameUiState);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        Map map = mapState.getMap();
        updateWalkDirection(map);
        updateVerticalDirection(map);
        map.update(tpf);
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
                        break;
                    case Input.Keys.J:
                        map.applyInput(new ActionInput());
                        break;
                    case Input.Keys.ENTER:
                        map.startNextRun();
                        break;
                    case Input.Keys.BACKSPACE:
                        mapState.startNewGame();
                        break;
                    case Input.Keys.ESCAPE:
                        mapState.openPauseMenu();
                        break;
                }
                return false;
            }
        };
    }
}
