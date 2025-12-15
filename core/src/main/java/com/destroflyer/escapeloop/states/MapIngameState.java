package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.inputs.ActionInput;
import com.destroflyer.escapeloop.game.inputs.JumpInput;
import com.destroflyer.escapeloop.game.inputs.SetVerticalDirectionInput;
import com.destroflyer.escapeloop.game.inputs.SetWalkDirectionInput;
import com.destroflyer.escapeloop.util.FloatUtil;
import com.destroflyer.escapeloop.util.InputUtil;

public class MapIngameState extends UiState {

    public MapIngameState(MapState mapState) {
        this.mapState = mapState;
        timeMachineChargeTextureRegion = new TextureRegion(new Texture("./textures/orange_robot/idle_with_time_machine.png"), 5, 12, 22, 22);
    }
    private MapState mapState;
    private Label timeMachineLabel;
    private Label infoLabel;
    private Label timeLabel;
    private TextureRegion timeMachineChargeTextureRegion;

    @Override
    public void create() {
        super.create();
        timeMachineLabel = new Label(null, main.getSkinLarge());
        timeMachineLabel.setPosition(20, (Main.VIEWPORT_HEIGHT - 36));
        stage.addActor(timeMachineLabel);

        infoLabel = new Label(null, main.getSkinSmall());
        // 1px lower than the other labels to look a bit more aligned
        infoLabel.setPosition(710, (Main.VIEWPORT_HEIGHT - 37));
        infoLabel.setAlignment(Align.center);
        stage.addActor(infoLabel);

        timeLabel = new Label(null, main.getSkinLarge());
        timeLabel.setPosition((Main.VIEWPORT_WIDTH - 230), (Main.VIEWPORT_HEIGHT - 36));
        stage.addActor(timeLabel);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        Map map = mapState.getMap();

        if (map.getCinematic() == null) {
            updateWalkDirection(map);
            updateVerticalDirection(map);
        }

        map.update();

        if (map.getCinematic() == null) {
            Preferences preferences = main.getSettingsState().getPreferences();

            String timeMachineText = "Time machine [" + InputUtil.getKeyName(preferences.getInteger("keyTimeMachine")) + "]:";
            if (map.getMaximumPlayerPasts() == 0) {
                timeMachineText += " No charges";
            }
            timeMachineLabel.setText(timeMachineText);

            String infoText = "";
            infoText += InputUtil.getKeyName(preferences.getInteger("keyAction")) + " = Action";
            infoText += ", ";
            infoText += InputUtil.getKeyName(preferences.getInteger("keyRespawn")) + " = Respawn";
            infoText += ", ";
            infoText += InputUtil.getKeyName(preferences.getInteger("keyReset")) + " = Reset";
            infoLabel.setText(infoText);

            timeLabel.setText("Time: " + FloatUtil.format(mapState.getMap().getTotalTime(), 3) + "s");
        } else {
            timeMachineLabel.setText("");
            infoLabel.setText("");
            timeLabel.setText("");
        }
    }

    private void updateWalkDirection(Map map) {
        int walkDirection = mapState.isDirectionLeft() ? -1 : (mapState.isDirectionRight() ? 1 : 0);
        if (walkDirection != map.getPlayer().getWalkDirection()) {
            map.applyInput(new SetWalkDirectionInput(walkDirection));
        }
    }

    private void updateVerticalDirection(Map map) {
        int verticalDirection = mapState.isDirectionDown() ? -1 : (mapState.isDirectionUp() ? 1 : 0);
        if (verticalDirection != map.getPlayer().getVerticalDirection()) {
            map.applyInput(new SetVerticalDirectionInput(verticalDirection));
        }
    }

    @Override
    public void render() {
        super.render();
        Map map = mapState.getMap();
        if (map.getCinematic() == null) {
            drawTimeMachineCharges(map);
        }
    }

    private void drawTimeMachineCharges(Map map) {
        int textureSize = 50;
        int margin = 10;
        float y = Main.VIEWPORT_HEIGHT - margin - textureSize;

        spriteBatch.begin();
        for (int i = 0; i < map.getMaximumPlayerPasts(); i++) {
            boolean hasTimeMachineCharge = (map.getMaximumPlayerPasts() - map.getPlayerPasts().size()) > i;
            spriteBatch.setColor(1, 1, 1, hasTimeMachineCharge ? 1 : 0.25f);
            float x = timeMachineLabel.getX() + timeMachineLabel.getPrefWidth() + margin + (i * textureSize);
            spriteBatch.draw(timeMachineChargeTextureRegion, x, y, textureSize, textureSize);
        }
        spriteBatch.end();
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                Preferences preferences = main.getSettingsState().getPreferences();
                Map map = mapState.getMap();
                if (keycode == Input.Keys.ESCAPE) {
                    mapState.openPauseMenu();
                    playButtonSound();
                    return true;
                } else if (keycode == preferences.getInteger("keyReset")) {
                    map.reset();
                    return true;
                } else if (map.getCinematic() == null) {
                    if (keycode == preferences.getInteger("keyJump")) {
                        map.applyInput(new JumpInput());
                        return true;
                    } else if (keycode == preferences.getInteger("keyAction")) {
                        map.applyInput(new ActionInput());
                        return true;
                    } else if (keycode == preferences.getInteger("keyRespawn")) {
                        map.respawnCurrentPlayer();
                        return true;
                    } else if (keycode == preferences.getInteger("keyTimeMachine")) {
                        map.tryStartNextPlayer();
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
