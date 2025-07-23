package com.destroflyer.escapeloop.game.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Json;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.Platform;
import com.destroflyer.escapeloop.game.loader.json.MapData;
import com.destroflyer.escapeloop.game.loader.json.MapDataEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class MapLoader {

    public MapLoader(Map map) {
        this.map = map;
        loadData();
        loadTerrain();
    }
    private static final int TILE_SIZE_DATA = 16;
    private static final float TILE_SIZE_MAP = 0.5f;
    private Map map;
    private MapData data;
    private ArrayList<int[]> terrain;

    public void loadData() {
        Json json = new Json();
        json.setIgnoreUnknownFields(true);
        data = json.fromJson(MapData.class, getFileHandle("data.json"));
    }

    private void loadTerrain() {
        terrain = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(getFileHandle("Terrain.csv").reader())) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cells = line.split(",");
                int[] column = new int[cells.length];
                for (int i = 0; i < column.length; i++) {
                    column[i] = Integer.parseInt(cells[i]);
                }
                terrain.add(column);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private FileHandle getFileHandle(String fileName) {
        return Gdx.files.internal("./maps/" + map.getName() + "/" + fileName);
    }

    public void loadObjects() {
        for (int tileY = 0; tileY < terrain.size(); tileY++) {
            int[] row = terrain.get(tileY);
            for (int tileX = 0; tileX < row.length; tileX++) {
                int gridValue = row[tileX];
                if (gridValue == 1) {
                    float mapX = ((tileX + 0.5f) * TILE_SIZE_MAP);
                    float mapY = ((((terrain.size() - 1) - tileY) + 0.5f) * TILE_SIZE_MAP);
                    Platform platform = new Platform(BodyDef.BodyType.StaticBody, TILE_SIZE_MAP, TILE_SIZE_MAP);
                    map.addObject(platform);
                    platform.getBody().setTransform(new Vector2(mapX, mapY), 0);
                }
            }
        }
    }

    public float getWidth() {
        return toMapSize(data.getWidth());
    }

    public Vector2 getPlayerStartPosition() {
        return getMapPosition(data.getEntities().getPlayer().get(0));
    }

    private Vector2 getMapPosition(MapDataEntity entity) {
        return new Vector2(toMapX(entity.getX()), toMapY(entity.getY()));
    }

    private float toMapX(int dataX) {
        return toMapSize(dataX + 0.5f);
    }

    private float toMapY(int dataY) {
        return toMapSize(((data.getHeight() - TILE_SIZE_DATA) - dataY) + 0.5f);
    }

    private float toMapSize(float dataSize) {
        return dataSize * (TILE_SIZE_MAP / TILE_SIZE_DATA);
    }
}
