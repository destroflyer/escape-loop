package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.loader.MapLoader;
import com.destroflyer.escapeloop.util.MapImport;

import java.io.File;

public class MapSelectionState extends UiState {

    private Table levelsTable;

    @Override
    protected void create(Skin skin) {
        Label titleLabel = new Label("Select a level", skin);
        titleLabel.setPosition((Main.VIEWPORT_WIDTH / 2f) - (titleLabel.getPrefWidth() / 2), (Main.VIEWPORT_HEIGHT / 2f) + 110);
        stage.addActor(titleLabel);

        levelsTable = new Table();
        stage.addActor(levelsTable);

        TextButton backButton = new TextButton("Back", skin);
        backButton.setPosition(Main.VIEWPORT_WIDTH - 120, 35);
        backButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                switchToState(main.getMainMenuState());
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
        int i = 0;
        for (File mapDirectory : new File(MapLoader.DIRECTORY).listFiles()) {
            String mapName = mapDirectory.getName();
            String mapTitle = mapName.replaceAll("_", " ");
            if ((i % 8) == 0) {
                levelsTable.row();
            }
            TextButton level = new TextButton(mapTitle, main.getSkin());
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
            i++;
        }
        levelsTable.setFillParent(true);
    }
}
