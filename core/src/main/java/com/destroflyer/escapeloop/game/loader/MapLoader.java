package com.destroflyer.escapeloop.game.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Json;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.game.objects.Bouncer;
import com.destroflyer.escapeloop.game.objects.Enemy;
import com.destroflyer.escapeloop.game.objects.Finish;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.objects.Gate;
import com.destroflyer.escapeloop.game.objects.Item;
import com.destroflyer.escapeloop.game.objects.Platform;
import com.destroflyer.escapeloop.game.loader.json.MapData;
import com.destroflyer.escapeloop.game.loader.json.MapDataEntity;
import com.destroflyer.escapeloop.game.objects.PressureTrigger;
import com.destroflyer.escapeloop.game.objects.Start;
import com.destroflyer.escapeloop.game.objects.ToggleTrigger;
import com.destroflyer.escapeloop.game.objects.items.DamageItem;
import com.destroflyer.escapeloop.game.objects.items.FreezeItem;
import com.destroflyer.escapeloop.game.objects.items.HeavyItem;
import com.destroflyer.escapeloop.game.objects.items.KnockbackItem;
import com.destroflyer.escapeloop.game.objects.items.SwapItem;
import com.destroflyer.escapeloop.util.ClassUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class MapLoader {

    public MapLoader(Map map) {
        this.map = map;
        loadData();
        loadTerrain();
    }
    public static final String DIRECTORY = "./maps/";
    private static final int TILE_SIZE_DATA = 16;
    private static final HashMap<String, Class<? extends Item>> ITEM_CLASSES = new HashMap<>();
    static {
        ITEM_CLASSES.put("Damage", DamageItem.class);
        ITEM_CLASSES.put("Freeze", FreezeItem.class);
        ITEM_CLASSES.put("Heavy", HeavyItem.class);
        ITEM_CLASSES.put("Knockback", KnockbackItem.class);
        ITEM_CLASSES.put("Swap", SwapItem.class);
    }
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
                    float mapX = ((tileX + 0.5f) * Map.TILE_SIZE);
                    float mapY = ((((terrain.size() - 1) - tileY) + 0.5f) * Map.TILE_SIZE);
                    Platform platform = new Platform(BodyDef.BodyType.StaticBody, Map.TILE_SIZE, Map.TILE_SIZE);
                    map.addObject(platform);
                    platform.getBody().setTransform(new Vector2(mapX, mapY), 0);
                }
            }
        }
        loadEntities(data.getEntities().getStart(), entity -> new Start(), entity -> new Vector2(0, 0));
        loadEntities(data.getEntities().getFinish(), entity -> new Finish(), entity -> new Vector2(0, 0));
        loadEntities(data.getEntities().getEnemy(), entity -> new Enemy(), entity -> new Vector2(0, 0));
        loadEntities(data.getEntities().getItem(), entity -> ClassUtil.newInstance(ITEM_CLASSES.get(entity.getCustomFields().getItem())), entity -> new Vector2(0, 0));
        loadEntities(data.getEntities().getBouncer(), entity -> new Bouncer(), entity -> new Vector2(0, -1 * (((16 - 4) / 2f) / 16)));
        loadEntities(data.getEntities().getToggle_Trigger(), entity -> new ToggleTrigger(), entity -> new Vector2(0, -1 * (((16 - 7) / 2f) / 16)));
        loadEntities(data.getEntities().getPressure_Trigger(), entity -> new PressureTrigger(), entity -> new Vector2(0, -1 * (((16 - 4) / 2f) / 16)));
        loadEntities(
            data.getEntities().getGate(),
            entity -> new Gate(toMapSize(entity.getWidth()), toMapSize(entity.getHeight())),
            entity -> new Vector2(((entity.getWidth() / TILE_SIZE_DATA) - 1) / 2f, ((entity.getHeight() / TILE_SIZE_DATA) - 1) / -2f)
        );
    }

    private void loadEntities(ArrayList<MapDataEntity> entities, Function<MapDataEntity, MapObject> createMapObject, Function<MapDataEntity, Vector2> getTileOffset) {
        if (entities != null) {
            for (MapDataEntity entity : entities) {
                MapObject mapObject = createMapObject.apply(entity);
                Vector2 tileOffset = getTileOffset.apply(entity);
                map.addObject(mapObject);
                mapObject.getBody().setTransform(getMapPosition(entity).add(tileOffset.scl(Map.TILE_SIZE)), 0);
            }
        }
    }

    public float getWidth() {
        return toMapSize(data.getWidth());
    }

    public Vector2 getStartPosition() {
        return getMapPosition(data.getEntities().getStart().get(0));
    }

    private Vector2 getMapPosition(MapDataEntity entity) {
        return new Vector2(toMapX(entity.getX()), toMapY(entity.getY()));
    }

    private float toMapX(int dataX) {
        return toMapSize(dataX + (TILE_SIZE_DATA / 2f));
    }

    private float toMapY(int dataY) {
        return toMapSize(((data.getHeight() - TILE_SIZE_DATA) - dataY) + (TILE_SIZE_DATA / 2f));
    }

    private float toMapSize(float dataSize) {
        return dataSize * (Map.TILE_SIZE / TILE_SIZE_DATA);
    }
}
