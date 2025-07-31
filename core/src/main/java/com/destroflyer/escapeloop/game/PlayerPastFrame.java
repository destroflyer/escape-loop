package com.destroflyer.escapeloop.game;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayerPastFrame {
    private float time;
    private Vector2 position;
    private ArrayList<PlayerInput> inputs;
}
