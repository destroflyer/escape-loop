package com.destroflyer.escapeloop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.destroflyer.escapeloop.states.MainMenuState;

import java.util.ArrayList;

import lombok.Getter;

public class Main extends ApplicationAdapter {

    public static final int VIEWPORT_WIDTH = 1280;
    public static final int VIEWPORT_HEIGHT = 720;

    @Getter
    private StretchViewport viewport;
    private InputMultiplexer inputMultiplexer;
    private ArrayList<State> states;
    @Getter
    private Skin skin;
    @Getter
    private MainMenuState mainMenu;

    @Override
    public void create() {
        viewport = new StretchViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);

        states = new ArrayList<>();

        skin = new Skin(Gdx.files.internal("vis/uiskin.json"));

        mainMenu = new MainMenuState();
        addState(mainMenu);
    }

    public void addState(State state) {
        state.createIfNeeded(this);
        states.add(state);
        InputProcessor inputProcessor = state.getInputProcessor();
        if (inputProcessor != null) {
            inputMultiplexer.addProcessor(inputProcessor);
        }
        for (State childState : state.getChildStates()) {
            addState(childState);
        }
    }

    public void removeState(State state) {
        states.remove(state);
        InputProcessor inputProcessor = state.getInputProcessor();
        if (inputProcessor != null) {
            inputMultiplexer.removeProcessor(inputProcessor);
        }
        for (State childState : state.getChildStates()) {
            removeState(childState);
        }
    }

    @Override
    public void render() {
        float tpf = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        for (State state : states) {
            state.update(tpf);
            state.render();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height, true);
        for (State state : states) {
            state.resize(viewport.getCamera().combined);
        }
    }

    @Override
    public void dispose() {
        for (State state : states) {
            state.dispose();
        }
    }
}
