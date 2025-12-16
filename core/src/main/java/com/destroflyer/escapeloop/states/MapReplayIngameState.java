package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.states.models.Highscore;
import com.destroflyer.escapeloop.util.TimeUtil;

public class MapReplayIngameState extends MapIngameState<ReplayMapState> {

    public MapReplayIngameState(ReplayMapState replayMapState) {
        super(replayMapState);
    }
    private Label highscoreLabel;

    @Override
    public void create() {
        super.create();
        Highscore highscore = mapState.getHighscore();
        highscoreLabel = new Label(null, main.getSkinLarge());
        highscoreLabel.setPosition((Main.VIEWPORT_WIDTH - 20), (Main.VIEWPORT_HEIGHT - 36));
        highscoreLabel.setAlignment(Align.right);
        highscoreLabel.setText(highscore.getUser() + ": " + TimeUtil.formatFrames(highscore.getFrames()));
        stage.addActor(highscoreLabel);
    }
}
