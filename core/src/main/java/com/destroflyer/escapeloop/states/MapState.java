package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.State;

import lombok.Getter;

public class MapState extends State {

    public MapState(String name) {
        this.name = name;
    }
    private String name;
    @Getter
    private Map map;
    private MapRenderState mapRenderState;
    private MapIngameState mapIngameState;
    private MapPauseState mapPauseState;
    @Getter
    private boolean isDirectionLeft;
    @Getter
    private boolean isDirectionRight;
    @Getter
    private boolean isDirectionUp;
    @Getter
    private boolean isDirectionDown;

    @Override
    public void create() {
        super.create();
        mapRenderState = new MapRenderState(this);
        mapIngameState = new MapIngameState(this);
        mapPauseState = new MapPauseState(this);
        childStates.add(mapRenderState);
        childStates.add(mapIngameState);
        childStates.add(mapPauseState);
        startNewGame();
    }

    @Override
    public void onAdd(Main main) {
        super.onAdd(main);
        main.addState(mapRenderState);
        main.addState(mapIngameState);
    }

    public void startNewGame() {
        map = new Map(name);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (map.isFinished()) {
            switchToState(main.getMapSelectionState());
        }
    }

    public void openPauseMenu() {
        main.removeState(mapIngameState);
        main.addState(mapPauseState);
    }

    public void closePauseMenu() {
        main.removeState(mapPauseState);
        main.addState(mapIngameState);
    }

    public void backToMapSelection() {
        switchToState(main.getMapSelectionState());
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.A:
                        isDirectionLeft = true;
                        break;
                    case Input.Keys.D:
                        isDirectionRight = true;
                        break;
                    case Input.Keys.W:
                        isDirectionUp = true;
                        break;
                    case Input.Keys.S:
                        isDirectionDown = true;
                        break;
                }
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                switch (keycode) {
                    case Input.Keys.A:
                        isDirectionLeft = false;
                        break;
                    case Input.Keys.D:
                        isDirectionRight = false;
                        break;
                    case Input.Keys.W:
                        isDirectionUp = false;
                        break;
                    case Input.Keys.S:
                        isDirectionDown = false;
                        break;
                }
                return false;
            }
        };
    }
}
