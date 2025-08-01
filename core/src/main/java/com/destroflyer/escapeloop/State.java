package com.destroflyer.escapeloop;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

import java.util.ArrayList;

import lombok.Getter;

public class State {

    protected Main main;
    @Getter
    protected ArrayList<State> childStates = new ArrayList<>();
    @Getter
    protected ArrayList<InputProcessor> inputProcessors = new ArrayList<>();
    protected SpriteBatch spriteBatch = new SpriteBatch();
    protected ShapeRenderer shapeRenderer = new ShapeRenderer();
    protected PolygonSpriteBatch polygonSpriteBatch = new PolygonSpriteBatch();

    public void onAdd(Main main) {
        if (this.main == null) {
            this.main = main;
            create();
        }
    }

    public void create() {
        InputProcessor inputProcessor = createInputProcessor();
        if (inputProcessor != null) {
            inputProcessors.add(inputProcessor);
        }
    }

    protected InputProcessor createInputProcessor() {
        return null;
    }

    public void update(float tpf) {

    }

    public void render() {

    }

    public void switchToState(State state) {
        main.removeState(this);
        main.addState(state);
    }

    public void resize(Matrix4 projectionMatrix) {
        spriteBatch.setProjectionMatrix(projectionMatrix);
        shapeRenderer.setProjectionMatrix(projectionMatrix);
        polygonSpriteBatch.setProjectionMatrix(projectionMatrix);
    }

    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        polygonSpriteBatch.dispose();
    }
}
