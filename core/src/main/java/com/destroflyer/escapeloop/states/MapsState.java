package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.destroflyer.escapeloop.State;
import com.destroflyer.escapeloop.game.loader.json.MapData;
import com.destroflyer.escapeloop.states.models.Highscore;

import java.util.ArrayList;

public class MapsState extends State {

    public static final int MAPS_COUNT = 100;

    private ArrayList<String> mapIds = new ArrayList<>();

    @Override
    public void create() {
        super.create();
        loadMapIds();
    }

    // FIXME: The file loader calls shouldn't happen in multiple places - Ideally all map file loadings are prepared in this state
    private void loadMapIds() {
        Json json = new Json();
        json.setIgnoreUnknownFields(true);
        for (int mapIndex = 0; mapIndex < MAPS_COUNT; mapIndex++) {
            MapData data = json.fromJson(MapData.class, Gdx.files.internal("./maps/" + mapIndex + "/data.json"));
            mapIds.add(data.getUniqueIdentifer());
        }
    }

    public boolean hasUnlockedMap(int mapIndex) {
        if (main.getSettingsState().getPreferences().getBoolean("unlockAllLevels")) {
            return true;
        }
        if (main.getDestrostudiosState().getPersonalRecords().containsKey(getMapId(mapIndex))) {
            return true;
        }
        return mapIndex <= this.getCurrentLevel();
    }

    public int getCurrentLevel() {
        for (int mapIndex = 0; mapIndex < MAPS_COUNT; mapIndex++) {
            String mapId = getMapId(mapIndex);
            Highscore highscore = main.getDestrostudiosState().getPersonalRecords().get(mapId);
            if (highscore == null) {
                return mapIndex;
            }
        }
        return MAPS_COUNT;
    }

    public String getMapId(int mapIndex) {
        return mapIds.get(mapIndex);
    }
}
