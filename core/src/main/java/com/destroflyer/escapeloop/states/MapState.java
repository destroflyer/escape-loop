package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.State;

import lombok.Getter;

public class MapState extends State {

    public MapState(String name) {
        this.name = name;
    }
    private String name;
    @Getter
    private Map map;
    @Getter
    private MapRenderState mapRenderState;
    private MapIngameState mapIngameState;
    private MapPauseState mapPauseState;
    @Getter
    private boolean isDirectionUp;
    @Getter
    private boolean isDirectionLeft;
    @Getter
    private boolean isDirectionDown;
    @Getter
    private boolean isDirectionRight;

    @Override
    public void create() {
        super.create();
        mapRenderState = new MapRenderState(this);
        mapIngameState = new MapIngameState(this);
        mapPauseState = new MapPauseState(this);
        childStates.add(mapRenderState);
        childStates.add(mapIngameState);
        childStates.add(mapPauseState);
        map = new Map(name);
    }

    @Override
    public void onAdd(Main main) {
        super.onAdd(main);
        main.addState(mapRenderState);
        main.addState(mapIngameState);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (map.isFinished()) {
            switchToState(main.getMapSelectionState());
        }
    }

    public void openPauseMenu() {
        main.removeState(mapIngameState);
        main.addState(mapPauseState);
    }

    public void closePauseMenu() {
        main.removeState(mapPauseState);
        main.addState(mapIngameState);
    }

    public void backToMapSelection() {
        switchToState(main.getMapSelectionState());
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                return onKey(keycode, true);
            }

            @Override
            public boolean keyUp(int keycode) {
                return onKey(keycode, false);
            }

            private boolean onKey(int keycode, boolean down) {
                Preferences preferences = main.getSettingsState().getPreferences();
                if (keycode == preferences.getInteger("keyUp")) {
                    isDirectionUp = down;
                    return true;
                } else if (keycode == preferences.getInteger("keyLeft")) {
                    isDirectionLeft = down;
                    return true;
                } else if (keycode == preferences.getInteger("keyDown")) {
                    isDirectionDown = down;
                    return true;
                } else if (keycode == preferences.getInteger("keyRight")) {
                    isDirectionRight = down;
                    return true;
                }
                return false;
            }
        };
    }
}
