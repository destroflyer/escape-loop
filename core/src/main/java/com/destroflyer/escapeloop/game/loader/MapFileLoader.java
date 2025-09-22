package com.destroflyer.escapeloop.game.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Json;
import com.destroflyer.escapeloop.game.Direction;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.game.MapText;
import com.destroflyer.escapeloop.game.behaviours.CircleMovementBehaviour;
import com.destroflyer.escapeloop.game.behaviours.HorizontalMovementBehaviour;
import com.destroflyer.escapeloop.game.behaviours.VerticalMovementBehaviour;
import com.destroflyer.escapeloop.game.loader.json.MapDataEntityCustomFieldEntity;
import com.destroflyer.escapeloop.game.loader.json.MapDataEntityCustomFields;
import com.destroflyer.escapeloop.game.objects.Bouncer;
import com.destroflyer.escapeloop.game.objects.Enemy;
import com.destroflyer.escapeloop.game.objects.Finish;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.objects.Gate;
import com.destroflyer.escapeloop.game.objects.Item;
import com.destroflyer.escapeloop.game.objects.Ground;
import com.destroflyer.escapeloop.game.loader.json.MapData;
import com.destroflyer.escapeloop.game.loader.json.MapDataEntity;
import com.destroflyer.escapeloop.game.objects.Platform;
import com.destroflyer.escapeloop.game.objects.PressureTrigger;
import com.destroflyer.escapeloop.game.objects.Scientist;
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
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MapFileLoader {

    public MapFileLoader(Map map) {
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
        return Gdx.files.internal("./maps/" + map.getMapIndex() + "/" + fileName);
    }

    public void loadContent() {
        for (int tileY = 0; tileY < terrain.size(); tileY++) {
            int[] row = terrain.get(tileY);
            for (int tileX = 0; tileX < row.length; tileX++) {
                int gridValue = row[tileX];
                if (gridValue == 1) {
                    float mapX = ((tileX + 0.5f) * Map.TILE_SIZE);
                    float mapY = ((((terrain.size() - 1) - tileY) + 0.5f) * Map.TILE_SIZE);
                    Ground ground = new Ground(BodyDef.BodyType.StaticBody, Map.TILE_SIZE, Map.TILE_SIZE);
                    map.addObject(ground);
                    ground.getBody().setTransform(new Vector2(mapX, mapY), 0);
                }
            }
        }

        HashMap<String, Gate> gatesByIid = new HashMap<>();
        Function<ArrayList<MapDataEntityCustomFieldEntity>, ArrayList<Gate>> getGates = (gateEntities) -> {
            ArrayList<Gate> gates = new ArrayList<>();
            for (MapDataEntityCustomFieldEntity gateEntity : gateEntities) {
                gates.add(gatesByIid.get(gateEntity.getEntityIid()));
            }
            return gates;
        };
        loadEntities(data.getEntities().getStart(), entity -> new Start(), entity -> new Vector2(0, 0), (entity, start) -> {
            start.setVisible(entity.getCustomFields().isVisible());
        });
        loadEntities(data.getEntities().getFinish(), entity -> new Finish(), entity -> new Vector2(0, 0));
        loadEntities(
            data.getEntities().getPlatform(),
            entity -> new Platform(BodyDef.BodyType.KinematicBody, toMapSize(entity.getWidth()), toMapSize(entity.getHeight())),
            entity -> new Vector2(((entity.getWidth() / TILE_SIZE_DATA) - 1) / 2f, ((entity.getHeight() / TILE_SIZE_DATA) - 1) / -2f),
            (entity, platform) -> {
                MapDataEntityCustomFields customFields = entity.getCustomFields();
                Vector2 position = platform.getBody().getPosition();
                if (customFields.getCircleMovementTileRadius() != null) {
                    platform.addBehaviour(new CircleMovementBehaviour(position.cpy(), customFields.getCircleMovementTileRadius() * Map.TILE_SIZE, customFields.getCircleMovementSpeed()));
                }
                if (customFields.getHorizontalMovementVelocity() != null) {
                    float x1 = position.x;
                    float x2 = position.x + (customFields.getHorizontalMovementTileWidth() * Map.TILE_SIZE);
                    float minX;
                    float maxX;
                    if (x1 < x2) {
                        minX = x1;
                        maxX = x2;
                    } else {
                        minX = x2;
                        maxX = x1;
                    }
                    platform.addBehaviour(new HorizontalMovementBehaviour(minX, maxX, customFields.getHorizontalMovementVelocity()));
                }
                if (customFields.getVerticalMovementVelocity() != null) {
                    float y1 = position.y;
                    float y2 = position.y + (customFields.getVerticalMovementTileHeight() * Map.TILE_SIZE);
                    float minY;
                    float maxY;
                    if (y1 < y2) {
                        minY = y1;
                        maxY = y2;
                    } else {
                        minY = y2;
                        maxY = y1;
                    }
                    platform.addBehaviour(new VerticalMovementBehaviour(minY, maxY, customFields.getVerticalMovementVelocity()));
                }
            }
        );
        loadEntities(data.getEntities().getEnemy(), entity -> new Enemy(entity.getCustomFields().getHoverTileHeight(), entity.getCustomFields().getShootCooldown(), entity.getCustomFields().isAutoShoot()), entity -> new Vector2(0, 0), (entity, enemy) -> {
            enemy.setViewDirection(entity.getCustomFields().getDirection().equals("Left") ? -1 : 1);
        });
        loadEntities(data.getEntities().getScientist(), entity -> new Scientist(), entity -> new Vector2(0, 0), (entity, scientist) -> {
            scientist.setViewDirection(entity.getCustomFields().getDirection().equals("Left") ? -1 : 1);
        });
        loadEntities(data.getEntities().getItem(), entity -> ClassUtil.newInstance(ITEM_CLASSES.get(entity.getCustomFields().getItem())), entity -> new Vector2(0, -1 * ((Map.TILE_SIZE - 0.15f) * Map.TILE_SIZE)));
        loadEntities(data.getEntities().getBouncer(), entity -> new Bouncer(), entity -> new Vector2(0, -1 * (((16 - 4) / 2f) / 16)));
        loadEntities(
            data.getEntities().getGate(),
            entity -> {
                Gate gate = new Gate(toMapSize(entity.getWidth()), toMapSize(entity.getHeight()));
                gatesByIid.put(entity.getIid(), gate);
                return gate;
            },
            entity -> new Vector2(((entity.getWidth() / TILE_SIZE_DATA) - 1) / 2f, ((entity.getHeight() / TILE_SIZE_DATA) - 1) / -2f)
        );
        loadEntities(data.getEntities().getToggle_Trigger(), entity -> new ToggleTrigger(getGates.apply(entity.getCustomFields().getGates())), entity -> {
            float alignmentOffsetX = 0.5f - ((7 / 2f) / 16);
            float alignmentOffsetY = -1 * (((16 - 7) / 2f) / 16);
            Vector2 tileOffset = new Vector2(0, 0);
            switch (entity.getCustomFields().getDirection()) {
                case "Left": tileOffset.x = alignmentOffsetX; break;
                case "Right": tileOffset.x = -1 * alignmentOffsetX; break;
                case "Up": tileOffset.y = alignmentOffsetY; break;
                case "Down": tileOffset.y = -1 * alignmentOffsetY; break;
            }
            return tileOffset;
        }, (entity, toggleTrigger) -> {
            float angle = 0;
            switch (entity.getCustomFields().getDirection()) {
                case "Left": angle = (float) (Math.PI / 2); break;
                case "Right": angle = (float) (Math.PI / -2); break;
                case "Down": angle = (float) Math.PI; break;
            }
            toggleTrigger.getBody().setTransform(toggleTrigger.getBody().getPosition(), angle);
        });
        loadEntities(data.getEntities().getPressure_Trigger(), entity -> new PressureTrigger(getGates.apply(entity.getCustomFields().getGates())), entity -> new Vector2(0, -1 * (((16 - 4) / 2f) / 16)));

        ArrayList<MapDataEntity> texts = data.getEntities().getText();
        if (texts != null) {
            for (MapDataEntity entity : texts) {
                Vector2 position = getMapPosition(entity);
                String text = entity.getCustomFields().getText();
                int width = entity.getCustomFields().getWidth();
                map.addText(new MapText(position, text, width));
            }
        }
    }

    private <T extends MapObject> void loadEntities(ArrayList<MapDataEntity> entities, Function<MapDataEntity, T> createMapObject, Function<MapDataEntity, Vector2> getTileOffset) {
        loadEntities(entities, createMapObject, getTileOffset, null);
    }

    private <T extends MapObject> void loadEntities(ArrayList<MapDataEntity> entities, Function<MapDataEntity, T> createMapObject, Function<MapDataEntity, Vector2> getTileOffset, BiConsumer<MapDataEntity, T> afterCreation) {
        if (entities != null) {
            for (MapDataEntity entity : entities) {
                T mapObject = createMapObject.apply(entity);
                Vector2 tileOffset = getTileOffset.apply(entity);
                map.addObject(mapObject);
                mapObject.getBody().setTransform(getMapPosition(entity).add(tileOffset.scl(Map.TILE_SIZE)), 0);
                if (afterCreation != null) {
                    afterCreation.accept(entity, mapObject);
                }
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

    public int getMaximumPlayerPasts() {
        return data.getCustomFields().getMaximumPlayerPasts();
    }

    private Direction parseDirection(String direction) {
        switch (direction) {
            case "Left": return Direction.LEFT;
            case "Right": return Direction.RIGHT;
            case "Down": return Direction.DOWN;
            case "Up": return Direction.UP;
        }
        throw new IllegalArgumentException(direction);
    }
}
