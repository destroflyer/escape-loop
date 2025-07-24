package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.State;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.inputs.JumpInput;
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
        map.update(tpf);
    }

    private void updateWalkDirection(Map map) {
        int walkDirection = mapState.isWalkingLeft() ? -1 : (mapState.isWalkingRight() ? 1 : 0);
        if (walkDirection != map.getPlayer().getWalkDirection()) {
            map.applyInput(new SetWalkDirectionInput(walkDirection));
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
