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
        Label titleLabel = new Label("Escape Loop", skin);
        titleLabel.setPosition((Main.VIEWPORT_WIDTH / 2f) - (titleLabel.getPrefWidth() / 2), (Main.VIEWPORT_HEIGHT / 2f) + 110);
        stage.addActor(titleLabel);

        Table menuTable = new Table();

        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                switchToState(main.getMapSelectionState());
            }
        });
        menuTable.add(playButton);

        TextButton exitWorld = new TextButton("Exit", skin);
        exitWorld.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        menuTable.add(exitWorld).padLeft(10);
        menuTable.setFillParent(true);
        menuTable.center();
        menuTable.moveBy(0, -130);

        stage.addActor(menuTable);
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
