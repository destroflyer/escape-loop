package com.destroflyer.escapeloop;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Matrix4;

import java.util.ArrayList;

import lombok.Getter;

public class State {

    protected Main main;
    @Getter
    protected ArrayList<State> childStates = new ArrayList<>();

    public void createIfNeeded(Main main) {
        if (this.main == null) {
            this.main = main;
            create();
        }
    }

    public void create() {

    }

    public void switchToState(State state) {
        main.removeState(this);
        main.addState(state);
    }

    public void update(float tpf) {

    }

    public void render() {

    }

    public void resize(Matrix4 projectionMatrix) {

    }

    public void dispose() {

    }

    public InputProcessor getInputProcessor() {
        return null;
    }
}
