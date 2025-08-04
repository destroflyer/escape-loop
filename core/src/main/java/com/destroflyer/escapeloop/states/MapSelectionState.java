package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.loader.MapLoader;
import com.destroflyer.escapeloop.util.MapImport;

import java.io.File;
import java.util.ArrayList;

public class MapSelectionState extends UiState {

    private static final String MAP_NAME_PREFIX = "Level_";
    private Table levelsTable;

    @Override
    public void create() {
        super.create();
        Label titleLabel = new Label("Select a level", main.getSkinLarge());
        titleLabel.setPosition((Main.VIEWPORT_WIDTH / 2f) - (titleLabel.getPrefWidth() / 2), (Main.VIEWPORT_HEIGHT / 2f) + 125);
        stage.addActor(titleLabel);

        levelsTable = new Table();
        stage.addActor(levelsTable);

        TextButton backButton = new TextButton("Back", main.getSkinLarge());
        backButton.setPosition(Main.VIEWPORT_WIDTH - 120, 35);
        backButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                backToMainMenu();
            }
        });
        stage.addActor(backButton);
    }

    @Override
    public void onAdd(Main main) {
        super.onAdd(main);
        if (MapImport.isSrcMapsDirectoryPathSet()) {
            MapImport.importAllMaps();
        }

        levelsTable.clear();
        ArrayList<String> mapNames = getMapNames();
        for (int i = 0; i < mapNames.size(); i++) {
            String mapName = mapNames.get(i);
            String mapTitle = mapName.replaceAll("_", " ");
            if ((i % 8) == 0) {
                levelsTable.row();
            }
            TextButton level = new TextButton(mapTitle, main.getSkinLarge());
            level.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (MapImport.isSrcMapsDirectoryPathSet()) {
                        MapImport.importMap(mapName);
                    }
                    switchToState(new MapState(mapName));
                }
            });
            levelsTable.add(level).fill().padRight(10).padBottom(10);
        }
        levelsTable.setFillParent(true);
    }

    private ArrayList<String> getMapNames() {
        ArrayList<String> mapNames = new ArrayList<>();
        for (File mapDirectory : new File(MapLoader.DIRECTORY).listFiles()) {
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

    private void backToMainMenu() {
        switchToState(main.getMainMenuState());
    }

    @Override
    public InputProcessor createInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.ESCAPE:
                        backToMainMenu();
                        return true;
                }
                return false;
            }
        };
    }
}
