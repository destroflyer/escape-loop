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
import com.destroflyer.escapeloop.util.TextureUtil;
import com.destroflyer.escapeloop.util.TimeUtil;

import java.util.ArrayList;
import java.util.function.Consumer;

public class MapSelectionState extends UiState {

    public static final float MAPS_COUNT = 100;
    private static final float MAPS_PER_ROW = 10;

    public MapSelectionState() {
        replayAvailableTextureRegion = TextureUtil.loadEyeIconTextureRegion(0);
        replayNotAvailableTextureRegion = TextureUtil.loadEyeIconTextureRegion(1);
    }
    private TextureRegion replayAvailableTextureRegion;
    private TextureRegion replayNotAvailableTextureRegion;
    private Label titleLabel;
    private Table mapsTable;
    private ArrayList<TextButton> mapButtons = new ArrayList<>();
    private int selectedMapIndex = -1;
    private int mapIndexToSelectAfterLoading;
    private String selectedMapId;
    private Label selectedMapLabel;
    private Image selectedMapImage;
    private RecordRow[] selectedMapWorldRecordRows;
    private RecordRow selectedMapPersonalRecordRow;
    private TextButton playButton;

    @Override
    public void create() {
        super.create();
        titleLabel = new Label(null, main.getSkinLarge());
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
        selectedMapTable.add(selectedMapLabel).colspan(3).width(playTableWidth).fill();

        selectedMapTable.row();
        selectedMapImage = new Image();
        selectedMapTable.add(selectedMapImage).colspan(3).size(playTableWidth, playTableWidth * (9f / 16)).padTop(10).fill();

        selectedMapTable.setPosition(Main.VIEWPORT_WIDTH - 55 - (selectedMapTable.getPrefWidth() / 2f), (Main.VIEWPORT_HEIGHT / 2f) - 20 + (selectedMapTable.getHeight() / 2));
        stage.addActor(selectedMapTable);

        Consumer<String> addRecordsTitleRow = (title) -> {
            selectedMapTable.row();
            selectedMapTable.add(new Label(title, main.getSkinSmall())).colspan(3).padTop(5).left();

            selectedMapTable.row();
            Image line = new Image(main.getSkinSmall().newDrawable("white", Color.WHITE));
            selectedMapTable.add(line).colspan(3).height(1).expandX().fillX();
        };

        addRecordsTitleRow.accept("World records");
        selectedMapWorldRecordRows = new RecordRow[DestrostudiosState.DISPLAYED_WORLD_RECORDS_PER_MAP];
        for (int i = 0; i < selectedMapWorldRecordRows.length; i++) {
            int _i = i;
            Label userLabel = new Label(null, main.getSkinSmall());
            Label timeLabel = new Label(null, main.getSkinSmall());
            Image replayImage = new Image();
            replayImage.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    ArrayList<Highscore> worldRecords = main.getDestrostudiosState().getWorldRecords().get(selectedMapId);
                    if ((worldRecords != null) && (_i < worldRecords.size())) {
                        Highscore highscore = worldRecords.get(_i);
                        if (highscore.getReplay() != null) {
                            switchToState(new ReplayMapState(selectedMapIndex, highscore));
                            playButtonSound();
                        }
                    }
                }
            });
            selectedMapTable.row();
            selectedMapTable.add(userLabel).left().expandX();
            selectedMapTable.add(timeLabel).right().expandX();
            selectedMapTable.add(replayImage).width(replayAvailableTextureRegion.getRegionWidth()).padTop(1).padLeft(5);
            selectedMapWorldRecordRows[i] = new RecordRow(userLabel, timeLabel, replayImage);
        }
        addRecordsTitleRow.accept("Personal record");
        selectedMapTable.row();

        Label personalRecordUserLabel = new Label(null, main.getSkinSmall());
        Label personalRecordTimeLabel = new Label(null, main.getSkinSmall());
        Image personalRecordReplayImage = new Image();
        personalRecordReplayImage.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Highscore personalRecord = main.getDestrostudiosState().getPersonalRecords().get(selectedMapId);
                if ((personalRecord != null) && (personalRecord.getReplay() != null)) {
                    switchToState(new ReplayMapState(selectedMapIndex, personalRecord));
                    playButtonSound();
                }
            }
        });
        selectedMapTable.row();
        selectedMapTable.add(personalRecordUserLabel).left().expandX();
        selectedMapTable.add(personalRecordTimeLabel).right().expandX();
        selectedMapTable.add(personalRecordReplayImage).width(replayAvailableTextureRegion.getRegionWidth()).padTop(1).padLeft(5);
        selectedMapPersonalRecordRow = new RecordRow(personalRecordUserLabel, personalRecordTimeLabel, personalRecordReplayImage);

        selectedMapTable.row();
        playButton = new TextButton("Play", main.getSkinLarge());
        playButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!playButton.isDisabled()) {
                    switchToState(new PlayMapState(selectedMapIndex));
                    playButtonSound();
                }
            }
        });
        selectedMapTable.add(playButton).colspan(3).padTop(5).width(playTableWidth).fill();
    }

    @Override
    public void onAdd(Main main) {
        super.onAdd(main);
        main.getDestrostudiosState().requestHighscores();

        if (MapImport.isSrcMapsDirectoryPathSet()) {
            MapImport.importAllMaps();
        }

        setTitle("Loading...");

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

        mapIndexToSelectAfterLoading = selectedMapIndex;
        selectMap(-1);

        playButton.setDisabled(true);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        initializeAfterLoading();
        updateRecords();
    }

    private void initializeAfterLoading() {
        if (playButton.isDisabled() && !main.getDestrostudiosState().isLoading()) {
            setTitle("Select a level");
            for (int mapIndex = 0; mapIndex < MAPS_COUNT; mapIndex++) {
                TextButton mapButton = mapButtons.get(mapIndex);
                boolean isUnlocked = main.getMapsState().hasUnlockedMap(mapIndex);
                mapButton.setDisabled(!isUnlocked);
            }
            if (mapIndexToSelectAfterLoading == -1) {
                int currentLevel = main.getMapsState().getCurrentLevel();
                mapIndexToSelectAfterLoading = (int) Math.min(currentLevel, MAPS_COUNT - 1);
            }
            selectMap(mapIndexToSelectAfterLoading);
            playButton.setDisabled(false);
        }
    }

    private void setTitle(String title) {
        titleLabel.setText(title);
        titleLabel.setPosition((Main.VIEWPORT_WIDTH / 2f) - (titleLabel.getPrefWidth() / 2), 615);
    }

    private void updateRecords() {
        ArrayList<Highscore> worldRecords = main.getDestrostudiosState().getWorldRecords().get(selectedMapId);
        for (int i = 0; i < selectedMapWorldRecordRows.length; i++) {
            Highscore highscore = (((worldRecords != null) && (i < worldRecords.size())) ? worldRecords.get(i) : null);
            updateRecord(selectedMapWorldRecordRows[i], highscore);
        }
        Highscore personalRecord = main.getDestrostudiosState().getPersonalRecords().get(selectedMapId);
        for (int i = 0; i < selectedMapWorldRecordRows.length; i++) {
            updateRecord(selectedMapPersonalRecordRow, personalRecord);
        }
    }

    private void updateRecord(RecordRow recordRow, Highscore highscore) {
        if (highscore != null) {
            recordRow.getUserLabel().setText(highscore.getUser());
            recordRow.getTimeLabel().setText(TimeUtil.formatFrames(highscore.getFrames()));
            recordRow.getReplayImage().setDrawable(new TextureRegionDrawable((highscore.getReplay() != null) ? replayAvailableTextureRegion : replayNotAvailableTextureRegion));
            recordRow.getReplayImage().setVisible(true);
        } else {
            recordRow.getUserLabel().setText("-");
            recordRow.getTimeLabel().setText("");
            recordRow.getReplayImage().setVisible(false);
        }
    }

    public void selectMap(int mapIndex) {
        if (selectedMapIndex != -1) {
            mapButtons.get(selectedMapIndex).setChecked(false);
        }
        selectedMapIndex = mapIndex;
        if (selectedMapIndex != -1) {
            selectedMapId = main.getMapsState().getMapId(selectedMapIndex);
            selectedMapLabel.setText(getMapTitle(selectedMapIndex));
            selectedMapImage.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture("maps/" + selectedMapIndex + "/terrain.png"))));
            mapButtons.get(selectedMapIndex).setChecked(true);
        } else {
            selectedMapId = null;
            selectedMapLabel.setText("");
            selectedMapImage.setDrawable(null);
        }
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
