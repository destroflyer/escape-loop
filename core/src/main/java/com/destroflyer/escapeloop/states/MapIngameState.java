package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.destroflyer.escapeloop.game.Map;

public class MapIngameState<MS extends MapState<?, ?>> extends UiState {

    public MapIngameState(MS mapState) {
        this.mapState = mapState;
    }
    protected MS mapState;

    @Override
    public void update(float tpf) {
        super.update(tpf);
        Map map = mapState.getMap();
        map.update();
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                Preferences preferences = main.getSettingsState().getPreferences();
                Map map = mapState.getMap();
                if (keycode == Input.Keys.ESCAPE) {
                    mapState.openPauseMenu();
                    playButtonSound();
                    return true;
                } else if (keycode == preferences.getInteger("keyReset")) {
                    map.reset();
                    return true;
                } else {
                    return onKeyDown(keycode);
                }
            }
        };
    }

    protected boolean onKeyDown(int keycode) {
        return false;
    }
}
