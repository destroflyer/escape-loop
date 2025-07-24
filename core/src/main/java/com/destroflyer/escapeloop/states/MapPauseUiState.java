package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.destroflyer.escapeloop.Main;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MapPauseUiState extends UiState {

    private MapState mapState;

    @Override
    protected void create(Skin skin) {
        Table menuTable = new Table();

        TextButton continueButton = new TextButton("Continue", skin);
        continueButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapState.closePauseMenu();
            }
        });
        menuTable.add(continueButton).fill();

        menuTable.row().padTop(10);

        TextButton exitButton = new TextButton("Back to level selection", skin);
        exitButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapState.backToMapSelection();
            }
        });
        menuTable.add(exitButton).fill();
        menuTable.setFillParent(true);
        menuTable.center();

        stage.addActor(menuTable);
    }

    @Override
    public void render() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.75f);
        shapeRenderer.rect(0, 0, Main.VIEWPORT_WIDTH, Main.VIEWPORT_HEIGHT);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        super.render();
    }
}
