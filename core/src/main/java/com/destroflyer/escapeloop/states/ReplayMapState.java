package com.destroflyer.escapeloop.states;

import com.destroflyer.escapeloop.game.ReplayMap;
import com.destroflyer.escapeloop.states.models.Highscore;

import lombok.Getter;

public class ReplayMapState extends MapState<ReplayMap, MapReplayIngameState> {

    public ReplayMapState(int mapIndex, Highscore highscore) {
        super(mapIndex);
        this.highscore = highscore;
    }
    @Getter
    private Highscore highscore;

    @Override
    protected MapReplayIngameState createMapIngameState() {
        return new MapReplayIngameState(this);
    }

    @Override
    protected ReplayMap createMap() {
        return new ReplayMap(mapIndex, this, main.getSettingsState(), main.getAudioState(), highscore.getReplay());
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (map.isFinished()) {
            switchToState(main.getMapSelectionState());
        }
    }
}
