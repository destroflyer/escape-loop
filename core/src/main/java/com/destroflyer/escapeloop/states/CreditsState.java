package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CreditsState extends UiState {

    @Override
    public void create() {
        super.create();
        Table table = new Table();

        Label titleLabel = new Label("Credits", main.getSkinLarge());
        table.add(titleLabel).colspan(2);

        addCredits(table, "Texture - Robots", "Edu");
        addCredits(table, "Texture - Scientists", "Elthen's Pixel Art Shop");
        addCredits(table, "Texture - Interior 1", "Chroma Dave");
        addCredits(table, "Texture - Interior 2", "Uma Alma");
        addCredits(table, "Music - Background", "BackgroundMusicForVideos");
        addCredits(table, "Music - Intro", "HitsLab");
        addCredits(table, "Sound - Alarm, Bounce, Bullet", "freesound_community");
        addCredits(table, "Sound - Explosion, Jump", "Brackeys");
        addCredits(table, "Sound - Loss, Win, Action", "floraphonic");
        addCredits(table, "Sound - Menu button", "Coffee 'Valen' Bat");
        addCredits(table, "Sound - Pickup", "Koi Roylers");
        addCredits(table, "Sound - Time machine", "DRAGON-STUDIO");
        addCredits(table, "Sound - Trigger", "Milan Wulf");
        addCredits(table, "Testing", "My friends <3");

        table.row().padTop(10);

        TextButton backButton = new TextButton("Ok", main.getSkinLarge());
        backButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                backToMainMenu();
            }
        });
        table.add(backButton).colspan(2).fill();

        table.setFillParent(true);
        table.center();

        stage.addActor(table);
    }

    private void addCredits(Table table, String labelText, String descriptionText) {
        table.row().padTop(10);

        Label label = new Label(labelText, main.getSkinSmall());
        table.add(label);

        Label description = new Label(descriptionText, main.getSkinSmall());
        table.add(description).padLeft(10);
    }

    private void backToMainMenu() {
        switchToState(main.getMainMenuState());
        playButtonSound();
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    backToMainMenu();
                    return true;
                }
                return false;
            }
        };
    }
}
