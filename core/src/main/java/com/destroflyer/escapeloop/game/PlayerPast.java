package com.destroflyer.escapeloop.game;

import java.util.ArrayList;

import lombok.Getter;

public class PlayerPast {

    public PlayerPast(ArrayList<PlayerInput> inputs) {
        this.inputs = inputs;
    }
    @Getter
    private Player player;
    private ArrayList<PlayerInput> inputs;
    private ArrayList<PlayerInput> remainingInputs = new ArrayList<>();

    public void reset() {
        player = new Player();
        remainingInputs.clear();
        remainingInputs.addAll(inputs);
    }

    public void applyInputs(float time) {
        for (int i = 0; i < remainingInputs.size(); i++) {
            PlayerInput input = remainingInputs.get(i);
            if (time >= input.getTime()) {
                input.apply(player);
                remainingInputs.remove(i);
                i--;
            } else {
                break;
            }
        }
    }
}
