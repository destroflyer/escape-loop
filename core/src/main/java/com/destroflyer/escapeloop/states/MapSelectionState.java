package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
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
import com.destroflyer.escapeloop.states.models.Highscore;
import com.destroflyer.escapeloop.states.models.RecordRow;
import com.destroflyer.escapeloop.util.MapImport;
import com.destroflyer.escapeloop.util.SkinUtil;
import com.destroflyer.escapeloop.util.TimeUtil;

import java.util.ArrayList;
import java.util.function.Consumer;

public class MapSelectionState extends UiState {

    public static final float MAPS_COUNT = 100;
    private static final float MAPS_PER_ROW = 10;

    private Table mapsTable;
    private ArrayList<TextButton> mapButtons = new ArrayList<>();
    private int selectedMapIndex;
    private String selectedMapId;
    private Label selectedMapLabel;
    private Image selectedMapImage;
    private RecordRow[] selectedMapWorldRecordRows;
    private RecordRow selectedMapPersonalRecordRow;
    private TextButton playButton;

    @Override
    public void create() {
        super.create();
        Label titleLabel = new Label("Select a level", main.getSkinLarge());
        titleLabel.setPosition((Main.VIEWPORT_WIDTH / 2f) - (titleLabel.getPrefWidth() / 2), 615);
        stage.addActor(titleLabel);

        mapsTable = new Table();
        stage.addActor(mapsTable);

        createSelectedMapTable();

        TextButton backButton = new TextButton("Back", main.getSkinLarge());
        backButton.setPosition(Main.VIEWPORT_WIDTH - 55 - backButton.getPrefWidth(), 30);
        backButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                backToMainMenu();
            }
        });
        stage.addActor(backButton);
    }

    private void createSelectedMapTable() {
        float playTableWidth = 400;
        Table selectedMapTable = new Table();

        selectedMapTable.row();
        selectedMapLabel = new Label(null, main.getSkinLarge());
        selectedMapLabel.setAlignment(Align.center);
        selectedMapTable.add(selectedMapLabel).colspan(2).width(playTableWidth).fill();

        selectedMapTable.row();
        selectedMapImage = new Image();
        selectedMapTable.add(selectedMapImage).colspan(2).size(playTableWidth, playTableWidth * (9f / 16)).padTop(10).fill();

        selectedMapTable.setPosition(Main.VIEWPORT_WIDTH - 55 - (selectedMapTable.getPrefWidth() / 2f), (Main.VIEWPORT_HEIGHT / 2f) - 20 + (selectedMapTable.getHeight() / 2));
        stage.addActor(selectedMapTable);

        Consumer<String> addRecordsTitleRow = (title) -> {
            selectedMapTable.row();
            selectedMapTable.add(new Label(title, main.getSkinSmall())).colspan(2).padTop(5).align(Align.left);

            selectedMapTable.row();
            Image line = new Image(main.getSkinSmall().newDrawable("white", Color.WHITE));
            selectedMapTable.add(line).colspan(2).height(1).expandX().fillX();
        };

        addRecordsTitleRow.accept("World records");
        selectedMapWorldRecordRows = new RecordRow[DestrostudiosState.DISPLAYED_WORLD_RECORDS_PER_MAP];
        for (int i = 0; i < selectedMapWorldRecordRows.length; i++) {
            Label userLabel = new Label(null, main.getSkinSmall());
            Label timeLabel = new Label(null, main.getSkinSmall());
            selectedMapTable.row();
            selectedMapTable.add(userLabel).align(Align.left);
            selectedMapTable.add(timeLabel).align(Align.right);
            selectedMapWorldRecordRows[i] = new RecordRow(userLabel, timeLabel);
        }
        addRecordsTitleRow.accept("Personal record");
        selectedMapTable.row();

        Label personalRecordUserLabel = new Label(null, main.getSkinSmall());
        Label personalRecordTimeLabel = new Label(null, main.getSkinSmall());
        selectedMapTable.row();
        selectedMapTable.add(personalRecordUserLabel).align(Align.left);
        selectedMapTable.add(personalRecordTimeLabel).align(Align.right);
        selectedMapPersonalRecordRow = new RecordRow(personalRecordUserLabel, personalRecordTimeLabel);

        selectedMapTable.row();
        playButton = new TextButton("Play", main.getSkinLarge());
        playButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!playButton.isDisabled()) {
                    switchToState(new MapState(selectedMapIndex));
                    playButtonSound();
                }
            }
        });
        selectedMapTable.add(playButton).colspan(2).padTop(5).width(playTableWidth).fill();
    }

    @Override
    public void onAdd(Main main) {
        super.onAdd(main);
        main.getDestrostudiosState().requestHighscores();

        if (MapImport.isSrcMapsDirectoryPathSet()) {
            MapImport.importAllMaps();
        }

        mapsTable.clear();
        mapButtons.clear();
        for (int mapIndex = 0; mapIndex < MAPS_COUNT; mapIndex++) {
            if ((mapIndex % MAPS_PER_ROW) == 0) {
                mapsTable.row();
            }
            TextButton mapButton = new TextButton(getMapTitle(mapIndex), SkinUtil.getToggleButtonStyle(main.getSkinLarge()));
            int _mapIndex = mapIndex;
            mapButton.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!mapButton.isDisabled()) {
                        selectMap(_mapIndex);
                        playButtonSound();
                    }
                }
            });
            mapButton.setDisabled(true);
            mapsTable.add(mapButton).fill().width(65).padRight(10).padBottom(10);
            mapButtons.add(mapButton);
        }
        mapsTable.setPosition(30 + (mapsTable.getPrefWidth() / 2f), 20 + (mapsTable.getPrefHeight() / 2));

        playButton.setDisabled(true);
    }

    @Override
    public void render() {
        super.render();
        updateMapButtons();
        updateRecords();
    }

    private void updateMapButtons() {
        if (playButton.isDisabled() && !main.getDestrostudiosState().isLoading()) {
            for (int mapIndex = 0; mapIndex < MAPS_COUNT; mapIndex++) {
                TextButton mapButton = mapButtons.get(mapIndex);
                boolean isUnlocked = main.getMapsState().hasUnlockedMap(mapIndex);
                mapButton.setDisabled(!isUnlocked);
            }
            int currentLevel = main.getMapsState().getCurrentLevel();
            selectMap((int) Math.min(currentLevel, MAPS_COUNT - 1));
            playButton.setDisabled(false);
        }
    }

    private void updateRecords() {
        ArrayList<Highscore> worldRecords = main.getDestrostudiosState().getWorldRecords().get(selectedMapId);
        for (int i = 0; i < selectedMapWorldRecordRows.length; i++) {
            RecordRow recordRow = selectedMapWorldRecordRows[i];
            Highscore highscore = (((worldRecords != null) && (i < worldRecords.size())) ? worldRecords.get(i) : null);
            recordRow.getUserLabel().setText((highscore != null) ? highscore.getUser() : "-");
            recordRow.getTimeLabel().setText((highscore != null) ? TimeUtil.formatMilliseconds(highscore.getTime()) : "-");
        }
        Highscore personalRecord = main.getDestrostudiosState().getPersonalRecords().get(selectedMapId);
        for (int i = 0; i < selectedMapWorldRecordRows.length; i++) {
            selectedMapPersonalRecordRow.getUserLabel().setText((personalRecord != null) ? personalRecord.getUser() : "-");
            selectedMapPersonalRecordRow.getTimeLabel().setText((personalRecord != null) ? TimeUtil.formatMilliseconds(personalRecord.getTime()) : "-");
        }
    }

    public void selectMap(int mapIndex) {
        mapButtons.get(selectedMapIndex).setChecked(false);
        selectedMapIndex = mapIndex;
        selectedMapId = main.getMapsState().getMapId(mapIndex);
        selectedMapLabel.setText(getMapTitle(selectedMapIndex));
        selectedMapImage.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture("maps/" + selectedMapIndex + "/terrain.png"))));
        mapButtons.get(selectedMapIndex).setChecked(true);
    }

    private String getMapTitle(int mapIndex) {
        return "" + (mapIndex + 1);
    }

    private void backToMainMenu() {
        switchToState(main.getMainMenuState());
        playButtonSound();
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
