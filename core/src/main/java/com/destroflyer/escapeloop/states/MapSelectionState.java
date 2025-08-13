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
import java.util.Comparator;
import java.util.HashMap;

public class MapSelectionState extends UiState {

    private Table mapsTable;
    private HashMap<Integer, TextButton> mapButtons = new HashMap<>();
    private Label selectedMapLabel;
    private Image selectedMapImage;
    private Integer selectedMapNumber;

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
                if (MapImport.isSrcMapsDirectoryPathSet()) {
                    MapImport.importMap(selectedMapNumber);
                }
                switchToState(new MapState(selectedMapNumber));
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

        mapsTable.clear();
        mapButtons.clear();
        ArrayList<Integer> mapNumbers = getMapNumbers();
        int currentLevel = getCurrentLevel();
        for (int i = 0; i < 50; i++) {
            if ((i % 5) == 0) {
                mapsTable.row();
            }
            boolean mapExists = i < mapNumbers.size();
            Integer mapNumber = mapExists ? mapNumbers.get(i) : null;
            boolean isSelectable = mapExists && (mapNumber <= currentLevel);
            TextButton mapButton = new TextButton(getMapTitle(mapNumber), SkinUtil.getToggleButtonStyle(main.getSkinLarge()));
            if (isSelectable) {
                mapButton.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        selectMap(mapNumber);
                    }
                });
            } else {
                mapButton.setDisabled(true);
            }
            mapsTable.add(mapButton).fill().padRight(10).padBottom(10);
            mapButtons.put(mapNumber, mapButton);
        }
        mapsTable.setPosition(50 + (mapsTable.getPrefWidth() / 2f), 40 + (mapsTable.getPrefHeight() / 2));

        selectMap(currentLevel);
    }

    private ArrayList<Integer> getMapNumbers() {
        ArrayList<Integer> mapNumbers = new ArrayList<>();
        for (File mapDirectory : new File(MapFileLoader.DIRECTORY).listFiles()) {
            mapNumbers.add(Integer.parseInt(mapDirectory.getName()));
        }
        mapNumbers.sort(Comparator.comparingInt(mapNumber -> mapNumber));
        return mapNumbers;
    }

    private int getCurrentLevel() {
        return main.getSettingsState().getPreferences().getInteger("level");
    }

    public void selectMap(int mapNumber) {
        if (selectedMapNumber != null) {
            mapButtons.get(selectedMapNumber).setChecked(false);
        }
        selectedMapNumber = mapNumber;
        selectedMapLabel.setText(getMapTitle(selectedMapNumber));
        selectedMapImage.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture("maps/" + selectedMapNumber + "/terrain.png"))));
        mapButtons.get(selectedMapNumber).setChecked(true);
    }

    private String getMapTitle(Integer mapNumber) {
        return (mapNumber != null) ? "Level " + mapNumber : "-";
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
