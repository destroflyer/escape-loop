package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Matrix4;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.State;
import com.destroflyer.escapeloop.game.inputs.JumpInput;
import com.destroflyer.escapeloop.game.inputs.SetWalkDirectionInput;

public class MapState extends State {

    private Map map;
    private MapRenderer mapRenderer;
    private boolean isWalkingLeft;
    private boolean isWalkingRight;

    @Override
    public void create() {
        super.create();
        startNewGame();
        childStates.add(new MapUiState());
    }

    private void startNewGame() {
        map = new Map("Level_0");
        mapRenderer = new MapRenderer(map);
        mapRenderer.resize(main.getViewport().getCamera().combined);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        map.update(tpf);
        mapRenderer.render();
    }

    @Override
    public void resize(Matrix4 projectionMatrix) {
        super.resize(projectionMatrix);
        mapRenderer.resize(projectionMatrix);
    }

    private void setWalkDirection() {
        map.applyInput(new SetWalkDirectionInput(isWalkingLeft ? -1 : (isWalkingRight ? 1 : 0)));
    }

    private InputProcessor inputProcessor = new InputAdapter() {

        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Input.Keys.A:
                    isWalkingLeft = true;
                    setWalkDirection();
                    break;
                case Input.Keys.D:
                    isWalkingRight = true;
                    setWalkDirection();
                    break;
                case Input.Keys.SPACE:
                    map.applyInput(new JumpInput());
                    break;
                case Input.Keys.ENTER:
                    map.startNextRun();
                    break;
                case Input.Keys.BACKSPACE:
                    startNewGame();
                    break;
                case Input.Keys.F1:
                    mapRenderer.setDebug(!mapRenderer.isDebug());
                    break;
                case Input.Keys.ESCAPE:
                    switchToState(main.getMainMenu());
                    break;
            }
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Input.Keys.A:
                    isWalkingLeft = false;
                    setWalkDirection();
                    break;
                case Input.Keys.D:
                    isWalkingRight = false;
                    setWalkDirection();
                    break;
            }
            return false;
        }
    };
    @Override
    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }
}
