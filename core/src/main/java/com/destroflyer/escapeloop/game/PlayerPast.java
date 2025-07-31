package com.destroflyer.escapeloop.game;

import com.destroflyer.escapeloop.game.objects.Player;

import java.util.ArrayList;

import lombok.Getter;

public class PlayerPast {

    public PlayerPast(ArrayList<PlayerPastFrame> frames) {
        this.frames = frames;
    }
    @Getter
    private Player player;
    private ArrayList<PlayerPastFrame> frames;
    @Getter
    private ArrayList<PlayerPastFrame> remainingFrames = new ArrayList<>();

    public void reset() {
        player = new Player();
        remainingFrames.clear();
        remainingFrames.addAll(frames);
    }

    public void applyFrames(float time) {
        for (int i = 0; i < remainingFrames.size(); i++) {
            PlayerPastFrame frame = remainingFrames.get(i);
            if (time >= frame.getTime()) {
                for (PlayerInput input : frame.getInputs()) {
                    input.apply(player);
                }
                remainingFrames.remove(i);
                i--;
            } else {
                break;
            }
        }
    }
}
