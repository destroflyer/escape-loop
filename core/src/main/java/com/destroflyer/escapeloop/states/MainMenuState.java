package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.Player;

public class MainMenuState extends UiState {

    @Override
    protected void create(Skin skin) {
        Label title = new Label("Escape Loop", skin);
        title.setPosition((Main.VIEWPORT_WIDTH / 2f) - (title.getPrefWidth() / 2), (Main.VIEWPORT_HEIGHT / 2f) + 110);
        stage.addActor(title);

        Table menu = new Table();

        TextButton play = new TextButton("Play", skin);
        play.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                switchToState(main.getMapSelectionState());
            }
        });
        menu.add(play);

        TextButton exit = new TextButton("Exit", skin);
        exit.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        menu.add(exit).padLeft(10);
        menu.setFillParent(true);
        menu.center();
        menu.moveBy(0, -130);

        stage.addActor(menu);
    }

    @Override
    public void render() {
        super.render();
        renderPlayer();
    }

    private void renderPlayer() {
        int width = 300;
        int height = 300;
        float x = (Main.VIEWPORT_WIDTH / 2f) - (width / 2f);
        float y = ((Main.VIEWPORT_HEIGHT / 2f) + 70) - (height / 2f);
        spriteBatch.setProjectionMatrix(main.getViewport().getCamera().combined);
        spriteBatch.begin();
        TextureRegion textureRegion = Player.ANIMATION_RUN.getKeyFrame(main.getTime(), true);
        spriteBatch.draw(textureRegion, x, y, width, height);
        spriteBatch.end();
    }
}
