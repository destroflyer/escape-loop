package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.destroflyer.escapeloop.game.PlayMap;
import com.destroflyer.escapeloop.game.replays.ReplayConverter;
import com.destroflyer.escapeloop.game.replays.json.Replay;

import lombok.Getter;

public class PlayMapState extends MapState<PlayMap, MapPlayIngameState> {

    public PlayMapState(int mapIndex) {
        super(mapIndex);
    }
    @Getter
    private boolean isDirectionUp;
    @Getter
    private boolean isDirectionLeft;
    @Getter
    private boolean isDirectionDown;
    @Getter
    private boolean isDirectionRight;

    @Override
    protected MapPlayIngameState createMapIngameState() {
        return new MapPlayIngameState(this);
    }

    @Override
    protected PlayMap createMap() {
        return new PlayMap(mapIndex, this, main.getSettingsState(), main.getAudioState());
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (map.isFinished()) {
            onMapFinished(tpf);
        }
    }

    private void onMapFinished(float tpf) {
        boolean wasCurrentLevel = mapIndex == main.getMapsState().getCurrentLevel();
        Replay replay = ReplayConverter.convertToReplay(map, tpf);
        main.getDestrostudiosState().requestSetHighscore(map.getId(), map.getTotalFrame(), replay);
        if (wasCurrentLevel) {
            main.getDestrostudiosState().requestHighscores();
            int nextMapIndex = mapIndex + 1;
            if (nextMapIndex <= MapSelectionState.MAPS_COUNT) {
                switchToState(new PlayMapState(nextMapIndex));
            } else {
                switchToState(main.getMapSelectionState());
            }
        } else {
            switchToState(new MapFinishedState(mapIndex, map.getTotalFrame()));
        }
        map.getAudioState().playSound("win");
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
