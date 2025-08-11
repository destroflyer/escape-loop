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
import java.util.HashMap;

public class MapSelectionState extends UiState {

    private static final String MAP_NAME_PREFIX = "Level_";
    private Table mapsTable;
    private HashMap<String, TextButton> mapButtons = new HashMap<>();
    private Label selectedMapLabel;
    private Image selectedMapImage;
    private String selectedMapName;

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
                    MapImport.importMap(selectedMapName);
                }
                switchToState(new MapState(selectedMapName));
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
        ArrayList<String> mapNames = getMapNames();
        for (int i = 0; i < 50; i++) {
            if ((i % 5) == 0) {
                mapsTable.row();
            }
            boolean mapExists = i < mapNames.size();
            String mapName = mapExists ? mapNames.get(i) : "-";
            TextButton mapButton = new TextButton(getMapTitle(mapName), SkinUtil.getToggleButtonStyle(main.getSkinLarge()));
            if (mapExists) {
                mapButton.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        selectMap(mapName);
                    }
                });
            } else {
                mapButton.setDisabled(true);
            }
            mapsTable.add(mapButton).fill().padRight(10).padBottom(10);
            mapButtons.put(mapName, mapButton);
        }
        mapsTable.setPosition(50 + (mapsTable.getPrefWidth() / 2f), 40 + (mapsTable.getPrefHeight() / 2));

        selectMap((selectedMapName != null) ? selectedMapName : MAP_NAME_PREFIX + "1");
    }

    private ArrayList<String> getMapNames() {
        ArrayList<String> mapNames = new ArrayList<>();
        for (File mapDirectory : new File(MapFileLoader.DIRECTORY).listFiles()) {
            mapNames.add(mapDirectory.getName());
        }
        mapNames.sort((mapName1, mapName2) -> {
            if (mapName1.startsWith(MAP_NAME_PREFIX) && mapName2.startsWith(MAP_NAME_PREFIX)) {
                int mapNumber1 = Integer.parseInt(mapName1.substring(MAP_NAME_PREFIX.length()));
                int mapNumber2 = Integer.parseInt(mapName2.substring(MAP_NAME_PREFIX.length()));
                return mapNumber1 - mapNumber2;
            }
            return mapName1.compareTo(mapName2);
        });
        return mapNames;
    }

    public void selectMap(String mapName) {
        if (selectedMapName != null) {
            mapButtons.get(selectedMapName).setChecked(false);
        }
        selectedMapName = mapName;
        selectedMapLabel.setText(getMapTitle(selectedMapName));
        selectedMapImage.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture("maps/" + mapName + "/terrain.png"))));
        mapButtons.get(selectedMapName).setChecked(true);
    }

    private String getMapTitle(String mapName) {
        return mapName.replaceAll("_", " ");
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
