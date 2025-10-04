package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.destroflyer.escapeloop.util.FloatUtil;
import com.destroflyer.escapeloop.util.InputUtil;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.Getter;
import lombok.Setter;

public class SettingsState extends UiState {

    public SettingsState() {
        preferences = Gdx.app.getPreferences("escape-loop");
        setDefaultFloat("volumeMaster", 0.1f);
        setDefaultFloat("volumeMusic", 1);
        setDefaultFloat("volumeSound", 1);
        setDefaultBoolean("playSoundMenuButton", true);
        setDefaultBoolean("playSoundEnemyShot", true);
        setDefaultFloat("playerPastsTrajectoryDuration", 3);
        setDefaultBoolean("playerPastsDistinctColors", false);
        setDefaultInteger("keyUp", Input.Keys.W);
        setDefaultInteger("keyLeft", Input.Keys.A);
        setDefaultInteger("keyDown", Input.Keys.S);
        setDefaultInteger("keyRight", Input.Keys.D);
        setDefaultInteger("keyJump", Input.Keys.SPACE);
        setDefaultInteger("keyAction", Input.Keys.J);
        setDefaultInteger("keyRespawn", Input.Keys.K);
        setDefaultInteger("keyTimeMachine", Input.Keys.L);
        setDefaultInteger("keyReset", Input.Keys.BACKSPACE);
        setDefaultInteger("level", 0);
    }
    @Getter
    private Preferences preferences;
    private Consumer<Integer> keyRecorder;
    @Setter
    private Runnable back;

    @Override
    public void create() {
        super.create();
        Table menuTable = new Table();

        Label titleLabel = new Label("Settings", main.getSkinLarge());
        menuTable.add(titleLabel).colspan(2);

        addSlider(menuTable, "Master volume", "volumeMaster", 0, 1, 0.01f, 2);
        addSlider(menuTable, "Music volume", "volumeMusic", 0, 2, 0.01f, 2);
        addSlider(menuTable, "Sound volume", "volumeSound", 0, 2, 0.01f, 2);
        addCheckbox(menuTable, "Play sound - Menu buttons", "playSoundMenuButton");
        addCheckbox(menuTable, "Play sound - Bullets", "playSoundEnemyShot");
        addSlider(menuTable, "Player pasts - Trajectory duration (s)", "playerPastsTrajectoryDuration", 0, 6, 0.1f, 1);
        addCheckbox(menuTable, "Player pasts - Distinct colors", "playerPastsDistinctColors");
        addKeyButton(menuTable, "Up", "keyUp");
        addKeyButton(menuTable, "Left", "keyLeft");
        addKeyButton(menuTable, "Down", "keyDown");
        addKeyButton(menuTable, "Right", "keyRight");
        addKeyButton(menuTable, "Jump", "keyJump");
        addKeyButton(menuTable, "Action", "keyAction");
        addKeyButton(menuTable, "Respawn", "keyRespawn");
        addKeyButton(menuTable, "Time machine", "keyTimeMachine");
        addKeyButton(menuTable, "Reset", "keyReset");

        menuTable.row().padTop(10);

        TextButton backButton = new TextButton("Ok", main.getSkinLarge());
        backButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                back();
                playButtonSound();
            }
        });
        menuTable.add(backButton).colspan(2).fill();

        menuTable.setFillParent(true);
        menuTable.center();

        stage.addActor(menuTable);
    }

    private void addSlider(Table menuTable, String labelText, String key, float minimum, float maximum, float stepSize, int displayedDecimals) {
        addElement(menuTable, null, label -> {
            Runnable updateLabel = () -> label.setText(labelText + ": " + FloatUtil.format(preferences.getFloat(key), displayedDecimals));
            updateLabel.run();

            Slider slider = new Slider(minimum, maximum, stepSize, false, main.getSkinSmall());
            slider.setValue(preferences.getFloat(key));
            slider.addListener(new ChangeListener() {

                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    preferences.putFloat(key, slider.getValue());
                    updateLabel.run();
                }
            });
            return slider;
        });
    }

    private void addCheckbox(Table menuTable, String labelText, String key) {
        addElement(menuTable, labelText, label -> {
            CheckBox checkbox = new CheckBox(null, main.getSkinSmall());
            checkbox.setChecked(preferences.getBoolean(key));
            checkbox.addListener(new ChangeListener() {

                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    preferences.putBoolean(key, checkbox.isChecked());
                }
            });
            return checkbox;
        });
    }

    private void addKeyButton(Table menuTable, String labelText, String key) {
        addElement(menuTable, labelText, label -> {
            TextButton button = new TextButton(null, main.getSkinSmall());

            Consumer<String> updateButtonText = customText -> button.setText((customText != null) ? customText : InputUtil.getKeyName(preferences.getInteger(key)));
            updateButtonText.accept(null);

            button.addListener(new ChangeListener() {

                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    updateButtonText.accept("Press a key");
                    keyRecorder = keycode -> {
                        if (keycode != null) {
                            preferences.putInteger(key, keycode);
                        }
                        updateButtonText.accept(null);
                    };
                }
            });
            return button;
        });
    }

    private void addElement(Table menuTable, String labelText, Function<Label, Actor> createElement) {
        menuTable.row().padTop(10);

        Label label = new Label(labelText, main.getSkinSmall());
        menuTable.add(label);

        Actor element = createElement.apply(label);
        menuTable.add(element).padLeft(10);
    }

    private void setDefaultBoolean(String key, boolean defaultValue) {
        if (!preferences.contains(key)) {
            preferences.putBoolean(key, defaultValue);
        }
    }

    private void setDefaultInteger(String key, int defaultValue) {
        if (!preferences.contains(key)) {
            preferences.putInteger(key, defaultValue);
        }
    }

    private void setDefaultFloat(String key, float defaultValue) {
        if (!preferences.contains(key)) {
            preferences.putFloat(key, defaultValue);
        }
    }

    public String replacePlaceholders(String text) {
        Map<String, ?> values = preferences.get();
        for (Map.Entry<String, ?> entry : values.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            if (key.startsWith("key")) {
                value = "[" + InputUtil.getKeyName(Integer.parseInt(value)) + "]";
            }
            text = text.replaceAll("\\$\\{" + entry.getKey() + "\\}", value);
        }
        return text;
    }

    private void back() {
        preferences.flush();
        if (keyRecorder != null) {
            stopKeyRecording(null);
        }
        main.removeState(this);
        back.run();
    }

    private void stopKeyRecording(Integer keycode) {
        keyRecorder.accept(keycode);
        keyRecorder = null;
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                if (keyRecorder != null) {
                    stopKeyRecording((keycode != Input.Keys.ESCAPE) ? keycode : null);
                    return true;
                } else if (keycode == Input.Keys.ESCAPE) {
                    back();
                    return true;
                }
                return false;
            }
        };
    }
}
