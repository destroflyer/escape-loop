package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.Cinematic;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.State;
import com.destroflyer.escapeloop.util.MapImport;

import lombok.Getter;

public class MapState extends State {

    public MapState(int mapIndex) {
        if (MapImport.isSrcMapsDirectoryPathSet()) {
            MapImport.importMap(mapIndex);
        }
        this.mapIndex = mapIndex;
    }
    private int mapIndex;
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
        map = new Map(mapIndex, main.getMusicState());
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
            onMapFinished();
        }
    }

    private void onMapFinished() {
        boolean goToMapSelection = true;
        Preferences preferences = main.getSettingsState().getPreferences();
        if (map.getMapIndex() >= preferences.getInteger("level")) {
            int nextMapIndex = map.getMapIndex() + 1;
            preferences.putInteger("level", nextMapIndex);
            preferences.flush();
            if (nextMapIndex < main.getMapSelectionState().getMaximumMapIndex()) {
                switchToState(new MapState(nextMapIndex));
                goToMapSelection = false;
            }
        }
        if (goToMapSelection) {
            switchToState(main.getMapSelectionState());
        }
    }

    public void openPauseMenu() {
        Cinematic cinematic = map.getCinematic();
        if (cinematic != null) {
            cinematic.onPauseMenuOpen();
        }
        main.removeState(mapIngameState);
        main.addState(mapPauseState);
    }

    public void closePauseMenu() {
        Cinematic cinematic = map.getCinematic();
        if (cinematic != null) {
            cinematic.onPauseMenuClose();
        }
        main.removeState(mapPauseState);
        main.addState(mapIngameState);
    }

    public void backToMapSelection() {
        Cinematic cinematic = map.getCinematic();
        if (cinematic != null) {
            cinematic.finish();
        }
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
