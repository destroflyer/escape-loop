package com.destroflyer.escapeloop.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.loader.MapCustomLoader;
import com.destroflyer.escapeloop.game.loader.MapFileLoader;
import com.destroflyer.escapeloop.game.objects.Player;
import com.destroflyer.escapeloop.states.MusicState;

import lombok.Getter;

import java.util.ArrayList;

public class Map {

    public Map(int mapIndex, MusicState musicState) {
        this.mapIndex = mapIndex;
        this.musicState = musicState;
        mapFileLoader = new MapFileLoader(this);
        mapCustomLoader = new MapCustomLoader(this);
        world = new World(GRAVITY, false);
        world.setContactListener(new MapContactListener(this));
        reset();
        cinematic = mapCustomLoader.getCinematic();
    }
    private static final Vector2 GRAVITY = new Vector2(0, -9.81f);
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITIONS_ITERATIONS = 2;
    public static final float TILE_SIZE = 0.5f;
    @Getter
    private int mapIndex;
    @Getter
    private MusicState musicState;
    private MapFileLoader mapFileLoader;
    private MapCustomLoader mapCustomLoader;
    @Getter
    public float width;
    @Getter
    private float totalTime;
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
    private int maximumPlayerPasts;
    @Getter
    private ArrayList<PlayerPast> playerPasts = new ArrayList<>();
    @Getter
    private ArrayList<MapText> texts = new ArrayList<>();
    @Getter
    private Cinematic cinematic;
    @Getter
    private boolean finished;

    public void tryStartNextPlayer() {
        if (playerPasts.size() < maximumPlayerPasts) {
            playerPasts.add(new PlayerPast(new ArrayList<>(currentPlayerFrames)));
            start();
        }
    }

    public void respawnCurrentPlayer() {
        if (playerPasts.isEmpty()) {
            reset();
        } else {
            start();
        }
    }

    public void reset() {
        totalTime = 0;
        playerPasts.clear();
        if (cinematic != null) {
            cinematic.finish();
        }
        cinematic = null;
        finished = false;
        start();
    }

    private void start() {
        time = 0;
        for (MapObject mapObject : objects) {
            world.destroyBody(mapObject.getBody());
        }
        objects.clear();
        queuedTasks.clear();
        texts.clear();

        width = mapFileLoader.getWidth();
        mapFileLoader.loadContent();
        Vector2 startPosition = mapFileLoader.getStartPosition();
        maximumPlayerPasts = mapFileLoader.getMaximumPlayerPasts();

        mapCustomLoader.loadContent();

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

    public void addText(MapText text) {
        texts.add(text);
    }

    public void update(float tpf) {
        totalTime += tpf;
        time += tpf;
        currentPlayerFrames.add(new PlayerPastFrame(time, player.getBody().getPosition().cpy(), new ArrayList<>(currentPlayerCurrentFrameInputs)));
        currentPlayerCurrentFrameInputs.clear();
        for (PlayerPast playerPast : playerPasts) {
            if (isPlayerAlive(playerPast.getPlayer())) {
                playerPast.applyTime(time);
            }
        }
        if (cinematic != null) {
            cinematic.applyTime(time);
            if (time >= cinematic.getDuration()) {
                reset();
            }
        }
        // Execute the physics step in two halves (one before and one after the game object updates), which ensures:
        // - In the first frame, the initial contacts are up-to-date when the game objects execute their updates (e.g. important for player characterCollisionsEnabled)
        // - When game objects change their fixtures during update (e.g. gates), they destroy the old fixture which immediately ends the contact. By executing another physics step afterwards, the new fixture contact gets started before rendering+inputs (e.g. important for character isGrounded)
        Runnable runHalfStep = () -> {
            world.step(tpf / 2f, VELOCITY_ITERATIONS, POSITIONS_ITERATIONS);
            runQueuedTasks();
        };
        runHalfStep.run();
        for (MapObject mapObject : objects) {
            mapObject.update(tpf);
        }
        runQueuedTasks();
        runHalfStep.run();
        if (!isPlayerAlive(player)) {
            respawnCurrentPlayer();
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

    public float getHeight() {
        return (((float) Main.VIEWPORT_HEIGHT) / Main.VIEWPORT_WIDTH) * width;
    }
}
