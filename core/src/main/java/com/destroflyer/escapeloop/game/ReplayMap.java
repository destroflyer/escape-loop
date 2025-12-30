package com.destroflyer.escapeloop.game;

import com.destroflyer.escapeloop.game.replays.ReplayConverter;
import com.destroflyer.escapeloop.game.replays.json.Replay;
import com.destroflyer.escapeloop.states.AudioState;
import com.destroflyer.escapeloop.states.MapState;
import com.destroflyer.escapeloop.states.SettingsState;

public class ReplayMap extends Map {

    public ReplayMap(int mapIndex, MapState<?, ?> mapState, SettingsState settingsState, AudioState audioState, Replay replay) {
        super(mapIndex, mapState, settingsState, audioState, loadSkins(replay));
        this.replay = replay;
    }
    private Replay replay;

    private static MapSkins loadSkins(Replay replay) {
        return new MapSkins(
            Skins.get(Skins.PLAYER, replay.getMetadata().getSkinPlayer()),
            Skins.get(Skins.ENEMY, replay.getMetadata().getSkinEnemy())
        );
    }

    @Override
    protected void loadContent() {
        super.loadContent();
        playerPasts.addAll(ReplayConverter.convertFromReplay(replay));
    }
}
