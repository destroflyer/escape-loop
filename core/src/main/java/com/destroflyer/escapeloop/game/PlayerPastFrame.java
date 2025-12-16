package com.destroflyer.escapeloop.game;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayerPastFrame {

    public PlayerPastFrame(int frame, ArrayList<PlayerInput> inputs) {
        this(frame, inputs, null);
    }
    private int frame;
    private ArrayList<PlayerInput> inputs;
    private Vector2 position;
}
