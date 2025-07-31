package com.destroflyer.escapeloop.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.destroflyer.escapeloop.game.loader.MapLoader;
import com.destroflyer.escapeloop.game.objects.Player;

import lombok.Getter;

import java.util.ArrayList;

public class Map {

    public Map(String name) {
        this.name = name;
        mapLoader = new MapLoader(this);
        world = new World(GRAVITY, false);
        world.setContactListener(new MapContactListener(this));
        reset();
    }
    private static final Vector2 GRAVITY = new Vector2(0, -9.81f);
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITIONS_ITERATIONS = 2;
    public static final float TILE_SIZE = 0.5f;
    @Getter
    private String name;
    private MapLoader mapLoader;
    @Getter
    public float width;
    @Getter
    private float time;
    @Getter
    private ArrayList<MapObject> objects = new ArrayList<>();
    private ArrayList<Runnable> queuedTasks = new ArrayList<>();
    @Getter
    private World world;
    @Getter
    private Player player;
    private ArrayList<PlayerInput> currentPlayerCurrentFrameInputs = new ArrayList<>();
    private ArrayList<PlayerPastFrame> currentPlayerFrames = new ArrayList<>();
    @Getter
    private ArrayList<PlayerPast> playerPasts = new ArrayList<>();
    @Getter
    private boolean finished;

    public void startNextRun() {
        playerPasts.add(new PlayerPast(new ArrayList<>(currentPlayerFrames)));
        reset();
    }

    public void onDeath() {
        playerPasts.clear();
        reset();
    }

    private void reset() {
        time = 0;
        for (MapObject mapObject : objects) {
            world.destroyBody(mapObject.getBody());
        }
        objects.clear();
        queuedTasks.clear();
        finished = false;

        width = mapLoader.getWidth();
        mapLoader.loadObjects();
        Vector2 startPosition = mapLoader.getStartPosition();

        player = new Player();
        addObject(player);
        player.getBody().setTransform(startPosition, 0);
        currentPlayerCurrentFrameInputs.clear();
        currentPlayerFrames.clear();

        for (PlayerPast playerPast : playerPasts) {
            playerPast.reset();
            addObject(playerPast.getPlayer());
            playerPast.getPlayer().getBody().setTransform(startPosition, 0);
        }
    }

    public void addObject(MapObject mapObject) {
        mapObject.setMap(this);
        mapObject.createBody();
        objects.add(mapObject);
    }

    public void removeObject(MapObject mapObject) {
        if (objects.remove(mapObject)) {
            world.destroyBody(mapObject.getBody());
        }
    }

    public void update(float tpf) {
        time += tpf;
        currentPlayerFrames.add(new PlayerPastFrame(time, player.getBody().getPosition().cpy(), new ArrayList<>(currentPlayerCurrentFrameInputs)));
        currentPlayerCurrentFrameInputs.clear();
        for (PlayerPast playerPast : playerPasts) {
            if (isPlayerAlive(playerPast.getPlayer())) {
                playerPast.applyFrames(time);
            }
        }
        world.step(tpf, VELOCITY_ITERATIONS, POSITIONS_ITERATIONS);
        runQueuedTasks();
        for (MapObject mapObject : objects) {
            mapObject.update(tpf);
        }
        runQueuedTasks();
        if (!isPlayerAlive(player)) {
            onDeath();
        }
    }

    private boolean isPlayerAlive(Player player) {
        return objects.contains(player);
    }

    public void queueTask(Runnable task) {
        queuedTasks.add(task);
    }

    private void runQueuedTasks() {
        for (Runnable queuedTask : queuedTasks) {
            queuedTask.run();
        }
        queuedTasks.clear();
    }

    public void applyInput(PlayerInput input) {
        input.apply(player);
        currentPlayerCurrentFrameInputs.add(input);
    }

    public void onFinish() {
        finished = true;
    }

    public MapObject getMapObject(Fixture fixture) {
        return getMapObject(fixture.getBody());
    }

    public MapObject getMapObject(Body body) {
        return objects.stream().filter(mapObject -> mapObject.getBody() == body).findAny().orElse(null);
    }
}
