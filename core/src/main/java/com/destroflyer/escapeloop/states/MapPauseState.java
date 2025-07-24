package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.State;

public class MapPauseState extends State {

    public MapPauseState(MapState mapState) {
        this.mapState = mapState;
    }
    private MapState mapState;
    private MapPauseUiState mapPauseUiState;

    @Override
    public void create() {
        super.create();
        mapPauseUiState = new MapPauseUiState(mapState);
        childStates.add(mapPauseUiState);
    }

    @Override
    public void onAdd(Main main) {
        super.onAdd(main);
        main.addState(mapPauseUiState);
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.ESCAPE:
                        mapState.closePauseMenu();
                        break;
                }
                return false;
            }
        };
    }
}
