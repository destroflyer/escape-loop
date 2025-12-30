package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.animations.PlayerAnimations;

public class MainMenuState extends UiState {

    @Override
    public void create() {
        super.create();
        Label titleLabel = new Label("Escape Loop", main.getSkinLarge());
        titleLabel.setPosition((Main.VIEWPORT_WIDTH / 2f) - (titleLabel.getPrefWidth() / 2), (Main.VIEWPORT_HEIGHT / 2f) + 110);
        stage.addActor(titleLabel);

        Table menuTable = new Table();

        addButton(menuTable, "Play", () -> {
            switchToState(main.getMapSelectionState());
            playButtonSound();
        });
        addButton(menuTable, "Settings", () -> {
            main.removeState(this);
            main.openSettings(() -> main.addState(this));
            playButtonSound();
        }).padLeft(10);
        addButton(menuTable, "Achievements", () -> {
            switchToState(main.getAchievementsState());
            playButtonSound();
        }).padLeft(10);
        addButton(menuTable, "Credits", () -> {
            switchToState(main.getCreditsState());
            playButtonSound();
        }).padLeft(10);
        addButton(menuTable, "Exit", () -> Gdx.app.exit()).padLeft(10);

        menuTable.setFillParent(true);
        menuTable.center();
        menuTable.moveBy(0, -130);

        stage.addActor(menuTable);

        Label userLabel = new Label(main.getAccount().getLogin(), main.getSkinSmall());
        userLabel.setPosition(Main.VIEWPORT_WIDTH - 10 - userLabel.getWidth(), Main.VIEWPORT_HEIGHT - 10 - userLabel.getHeight());
        stage.addActor(userLabel);
    }

    private Cell<TextButton> addButton(Table menuTable, String label, Runnable onClick) {
        TextButton button = new TextButton(label, main.getSkinLarge());
        button.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClick.run();
            }
        });
        return menuTable.add(button);
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
        Animation<TextureRegion> animation = PlayerAnimations.get(main.getSkinsState().getSelectedPlayerSkin()).getAnimationsWithTimeMachine().getRunAnimation();
        TextureRegion textureRegion = animation.getKeyFrame(main.getTime(), true);
        spriteBatch.draw(textureRegion, x, y, width, height);
        spriteBatch.end();
    }
}
