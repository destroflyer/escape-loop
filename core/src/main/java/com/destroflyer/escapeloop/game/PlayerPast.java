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
    @Getter
    private ArrayList<PlayerPastFrame> frames;
    @Getter
    private ArrayList<PlayerPastFrame> remainingFrames = new ArrayList<>();

    public void reset() {
        player = new Player();
        remainingFrames.clear();
        remainingFrames.addAll(frames);
    }

    public void applyFrame(int frame) {
        for (int i = 0; i < remainingFrames.size(); i++) {
            PlayerPastFrame pastFrame = remainingFrames.get(i);
            if (frame >= pastFrame.getFrame()) {
                for (PlayerInput input : pastFrame.getInputs()) {
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
