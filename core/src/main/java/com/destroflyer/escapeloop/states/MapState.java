package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.State;
import com.destroflyer.escapeloop.game.inputs.JumpInput;
import com.destroflyer.escapeloop.game.inputs.SetWalkDirectionInput;

import lombok.Getter;

public class MapState extends State {

    public MapState(String name) {
        this.name = name;
    }
    private String name;
    @Getter
    private Map map;
    private boolean isWalkingLeft;
    private boolean isWalkingRight;

    @Override
    public void create() {
        super.create();
        childStates.add(new MapRenderState(this));
        childStates.add(new MapUiState());
        startNewGame();
    }

    private void startNewGame() {
        map = new Map(name);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        map.update(tpf);
        if (map.isFinished()) {
            switchToState(main.getMapSelectionState());
        }
    }

    private void setWalkDirection() {
        map.applyInput(new SetWalkDirectionInput(isWalkingLeft ? -1 : (isWalkingRight ? 1 : 0)));
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

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
                    case Input.Keys.ESCAPE:
                        switchToState(main.getMapSelectionState());
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
    }
}
