package com.destroflyer.escapeloop.game;

import com.destroflyer.escapeloop.game.replays.ReplayConverter;
import com.destroflyer.escapeloop.game.replays.json.Replay;
import com.destroflyer.escapeloop.states.AudioState;
import com.destroflyer.escapeloop.states.MapState;
import com.destroflyer.escapeloop.states.SettingsState;

public class ReplayMap extends Map {

    public ReplayMap(int mapIndex, MapState<?, ?> mapState, SettingsState settingsState, AudioState audioState, Replay replay) {
        super(mapIndex, mapState, settingsState, audioState);
        this.replay = replay;
    }
    private Replay replay;

    @Override
    protected void loadContent() {
        super.loadContent();
        playerPasts.addAll(ReplayConverter.convertFromReplay(replay));
    }
}
