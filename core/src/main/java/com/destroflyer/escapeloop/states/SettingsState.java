package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.destroflyer.escapeloop.util.FloatUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.Setter;

public class SettingsState extends UiState {

    @Getter
    private float musicVolume = 0.1f;
    @Getter
    private float playerPastsTrajectoryDuration = 3;
    @Getter
    private boolean playerPastsDistinctColors;
    @Setter
    private Runnable back;

    @Override
    protected void create(Skin skin) {
        Table menuTable = new Table();

        Label titleLabel = new Label("Settings", skin);
        menuTable.add(titleLabel).colspan(2);

        menuTable.row().padTop(10);

        addSlider(menuTable, skin, "Music volume", () -> musicVolume, value -> musicVolume = value, 0, 1, 0.01f, 2);

        menuTable.row().padTop(10);

        addSlider(menuTable, skin, "Player pasts - Trajectory duration (s)", () -> playerPastsTrajectoryDuration, value -> playerPastsTrajectoryDuration = value, 0, 6, 0.1f, 1);

        menuTable.row().padTop(10);

        addCheckbox(menuTable, skin, "Player pasts - Distinct colors", () -> playerPastsDistinctColors, value -> playerPastsDistinctColors = value);

        menuTable.row().padTop(10);

        TextButton backButton = new TextButton("Ok", skin);
        backButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                back();
            }
        });
        menuTable.add(backButton).colspan(2).fill();

        menuTable.setFillParent(true);
        menuTable.center();

        stage.addActor(menuTable);
    }

    private void addSlider(Table menuTable, Skin skin, String label, Supplier<Float> getValue, Consumer<Float> setValue, float minimum, float maximum, float stepSize, int displayedDecimals) {
        Label sliderLabel = new Label(null, skin);
        Runnable updateSliderLabel = () -> sliderLabel.setText(label + ": " + FloatUtil.format(getValue.get(), displayedDecimals));
        updateSliderLabel.run();
        menuTable.add(sliderLabel);

        Slider slider = new Slider(minimum, maximum, stepSize, false, skin);
        slider.setValue(getValue.get());
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setValue.accept(slider.getValue());
                updateSliderLabel.run();
            }
        });
        menuTable.add(slider).padLeft(10);
    }

    private void addCheckbox(Table menuTable, Skin skin, String label, Supplier<Boolean> getValue, Consumer<Boolean> setValue) {
        Label checkboxLabel = new Label(label, skin);
        menuTable.add(checkboxLabel);

        CheckBox checkbox = new CheckBox(null, skin);
        checkbox.setChecked(getValue.get());
        checkbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setValue.accept(checkbox.isChecked());
            }
        });
        menuTable.add(checkbox).padLeft(10);
    }

    private void back() {
        main.removeState(this);
        back.run();
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.ESCAPE:
                        back();
                        return true;
                }
                return false;
            }
        };
    }
}
