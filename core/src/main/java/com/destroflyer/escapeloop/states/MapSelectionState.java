package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.loader.MapFileLoader;
import com.destroflyer.escapeloop.util.MapImport;
import com.destroflyer.escapeloop.util.SkinUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import lombok.Getter;

public class MapSelectionState extends UiState {

    private static final float MAXIMUM_DISPLAYED_MAPS = 88;
    private static final float MAPS_PER_ROW = 8;

    @Getter
    private int maximumMapIndex;
    private Table mapsTable;
    private ArrayList<TextButton> mapButtons = new ArrayList<>();
    private Label selectedMapLabel;
    private Image selectedMapImage;
    private Integer selectedMapIndex;

    @Override
    public void create() {
        super.create();
        Label titleLabel = new Label("Select a level", main.getSkinLarge());
        titleLabel.setPosition((Main.VIEWPORT_WIDTH / 2f) - (titleLabel.getPrefWidth() / 2), 624);
        stage.addActor(titleLabel);

        mapsTable = new Table();
        stage.addActor(mapsTable);

        createSelectedMapTable();

        TextButton backButton = new TextButton("Back", main.getSkinLarge());
        backButton.setPosition(Main.VIEWPORT_WIDTH - 50 - backButton.getPrefWidth(), 50);
        backButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                backToMainMenu();
            }
        });
        stage.addActor(backButton);
    }

    private void createSelectedMapTable() {
        float playTableWidth = 454;
        Table selectedMapTable = new Table();

        selectedMapTable.row();
        selectedMapLabel = new Label(null, main.getSkinLarge());
        selectedMapLabel.setAlignment(Align.center);
        selectedMapTable.add(selectedMapLabel).width(playTableWidth).fill();

        selectedMapTable.row();
        selectedMapImage = new Image();
        selectedMapTable.add(selectedMapImage).size(playTableWidth, playTableWidth * (9f / 16)).padTop(10).fill();

        selectedMapTable.row();
        TextButton playButton = new TextButton("Play", main.getSkinLarge());
        playButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                switchToState(new MapState(selectedMapIndex));
            }
        });
        selectedMapTable.add(playButton).width(playTableWidth).fill();

        selectedMapTable.setPosition(Main.VIEWPORT_WIDTH - 50 - (selectedMapTable.getPrefWidth() / 2f), (Main.VIEWPORT_HEIGHT / 2f) + (selectedMapTable.getHeight() / 2));
        stage.addActor(selectedMapTable);
    }

    @Override
    public void onAdd(Main main) {
        super.onAdd(main);
        if (MapImport.isSrcMapsDirectoryPathSet()) {
            MapImport.importAllMaps();
        }
        maximumMapIndex = findMaximumMapIndex();

        mapsTable.clear();
        mapButtons.clear();
        int currentLevel = getCurrentLevel();
        for (int mapIndex = 0; mapIndex < MAXIMUM_DISPLAYED_MAPS; mapIndex++) {
            if ((mapIndex % MAPS_PER_ROW) == 0) {
                mapsTable.row();
            }
            boolean mapExists = mapIndex <= maximumMapIndex;
            boolean isSelectable = mapExists && (mapIndex <= currentLevel);
            TextButton mapButton = new TextButton(getMapTitle(mapIndex), SkinUtil.getToggleButtonStyle(main.getSkinLarge()));
            if (isSelectable) {
                int _mapIndex = mapIndex;
                mapButton.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        selectMap(_mapIndex);
                    }
                });
            } else {
                mapButton.setDisabled(true);
            }
            mapsTable.add(mapButton).fill().padRight(10).padBottom(10);
            mapButtons.add(mapButton);
        }
        mapsTable.setPosition(30 + (mapsTable.getPrefWidth() / 2f), 20 + (mapsTable.getPrefHeight() / 2));

        selectMap(currentLevel);
    }

    private int findMaximumMapIndex() {
        return Arrays.stream(new File(MapFileLoader.DIRECTORY).listFiles())
            .map(File::getName)
            .mapToInt(Integer::parseInt)
            .max()
            .getAsInt();
    }

    private int getCurrentLevel() {
        return main.getSettingsState().getPreferences().getInteger("level");
    }

    public void selectMap(int mapIndex) {
        if (selectedMapIndex != null) {
            mapButtons.get(selectedMapIndex).setChecked(false);
        }
        selectedMapIndex = mapIndex;
        selectedMapLabel.setText(getMapTitle(selectedMapIndex));
        selectedMapImage.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture("maps/" + selectedMapIndex + "/terrain.png"))));
        mapButtons.get(selectedMapIndex).setChecked(true);
    }

    private String getMapTitle(Integer mapIndex) {
        return (mapIndex != null) ? "# " + (mapIndex + 1) : "-";
    }

    private void backToMainMenu() {
        switchToState(main.getMainMenuState());
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
