package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuState extends UiState {

    @Override
    protected void create(Skin skin, float width, float height) {
        Table menu = new Table();

        TextButton play = new TextButton("Play", skin);
        play.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                switchToState(new MapState());
            }
        });
        menu.add(play).padRight(10);

        TextButton exit = new TextButton("Exit", skin);
        exit.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        menu.add(exit);
        menu.setFillParent(true);
        menu.center();

        stage.addActor(menu);
    }
}
