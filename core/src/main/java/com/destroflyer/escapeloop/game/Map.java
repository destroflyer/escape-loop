package com.destroflyer.escapeloop.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.contact.MapContactListener;
import com.destroflyer.escapeloop.game.loader.MapCustomLoader;
import com.destroflyer.escapeloop.game.loader.MapFileLoader;
import com.destroflyer.escapeloop.game.objects.Player;
import com.destroflyer.escapeloop.states.MapState;
import com.destroflyer.escapeloop.states.AudioState;
import com.destroflyer.escapeloop.states.SettingsState;
import com.destroflyer.escapeloop.util.TimeUtil;

import lombok.Getter;

import java.util.ArrayList;

public class Map {

    public Map(int mapIndex, MapState<?, ?> mapState, SettingsState settingsState, AudioState audioState, MapSkins skins) {
        this.mapIndex = mapIndex;
        this.mapState = mapState;
        this.settingsState = settingsState;
        this.audioState = audioState;
        this.skins = skins;
    }
    private static final Vector2 GRAVITY = new Vector2(0, -9.81f);
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITIONS_ITERATIONS = 2;
    public static final float TILE_SIZE = 0.5f;
    @Getter
    private int mapIndex;
    @Getter
    private MapState<?, ?> mapState;
    @Getter
    private SettingsState settingsState;
    @Getter
    private MapSkins skins;
    @Getter
    protected AudioState audioState;
    protected MapFileLoader mapFileLoader;
    private MapCustomLoader mapCustomLoader;
    private MapContactListener mapContactListener;
    @Getter
    public float width;
    @Getter
    private int totalFrame;
    protected int frame;
    @Getter
    private ArrayList<MapObject> objects = new ArrayList<>();
    private int nextObjectId;
    private ArrayList<QueuedTask> queuedTasks = new ArrayList<>();
    @Getter
    private World world;
    @Getter
    protected ArrayList<PlayerPast> playerPasts = new ArrayList<>();
    @Getter
    private ArrayList<MapText> texts = new ArrayList<>();
    @Getter
    private Cinematic cinematic;
    @Getter
    private boolean finished;

    public void initialize() {
        mapFileLoader = new MapFileLoader(this);
        mapCustomLoader = new MapCustomLoader(this);
        mapContactListener = new MapContactListener(this);
        world = new World(GRAVITY, false);
        world.setContactListener(mapContactListener);
        reset();
        cinematic = mapCustomLoader.getCinematic();
    }

    public void reset() {
        mapCustomLoader.reset();
        totalFrame = 0;
        playerPasts.clear();
        if (cinematic != null) {
            cinematic.finish();
        }
        cinematic = null;
        finished = false;
        start();
    }

    protected void start() {
        frame = 0;
        for (MapObject mapObject : objects) {
            world.destroyBody(mapObject.getBody());
        }
        objects.clear();
        nextObjectId = 0;
        queuedTasks.clear();
        texts.clear();
        width = mapFileLoader.getWidth();
        loadContent();
        createPlayers();
    }

    protected void loadContent() {
        mapFileLoader.loadContent();
        mapCustomLoader.loadContent();
    }

    protected void createPlayers() {
        Vector2 startPosition = mapFileLoader.getStartPosition();
        for (PlayerPast playerPast : playerPasts) {
            playerPast.reset();
            addObject(playerPast.getPlayer());
            playerPast.getPlayer().getBody().setTransform(startPosition, 0);
        }
    }

    public void addObject(MapObject mapObject) {
        mapObject.setId(nextObjectId++);
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

    public void update() {
        float tpf = 1f / Main.FPS;
        updateQueuedTasks(tpf);
        updatePlayers();
        if (cinematic != null) {
            float time = getTime();
            cinematic.applyTime(time);
            if (time >= cinematic.getDuration()) {
                reset();
            }
        }
        // Execute the physics step in two halves (one before and one after the game logic updates), which ensures:
        // - In the first frame, the initial contacts are up-to-date when the game objects execute their updates (e.g. important for player characterCollisionsEnabled)
        // - When game objects change their fixtures during update (e.g. gates), they destroy the old fixture which immediately ends the contact. By executing another physics step afterwards, the new fixture contact gets started before rendering+inputs (e.g. important for character isGrounded)
        Runnable runHalfStep = () -> {
            world.step(tpf / 2f, VELOCITY_ITERATIONS, POSITIONS_ITERATIONS);
            mapContactListener.handleContacts();
            runQueuedTasks();
        };
        runHalfStep.run();
        for (MapObject mapObject : objects) {
            mapObject.update(tpf);
        }
        mapCustomLoader.update();
        runQueuedTasks();
        runHalfStep.run();
        totalFrame++;
        frame++;
    }

    protected void updatePlayers() {
        for (PlayerPast playerPast : playerPasts) {
            if (isPlayerAlive(playerPast.getPlayer())) {
                playerPast.applyFrame(frame);
            }
        }
    }

    protected boolean isPlayerAlive(Player player) {
        return objects.contains(player);
    }

    public void queueTask(Runnable runnable) {
        queueTask(runnable, 0);
    }

    public void queueTask(Runnable runnable, float remainingTime) {
        queuedTasks.add(new QueuedTask(runnable, remainingTime));
    }

    private void updateQueuedTasks(float tpf) {
        for (QueuedTask queuedTask : queuedTasks) {
            queuedTask.update(tpf);
        }
    }

    private void runQueuedTasks() {
        for (int i = 0; i < queuedTasks.size(); i++) {
            QueuedTask queuedTask = queuedTasks.get(i);
            if (queuedTask.shouldRun()) {
                queuedTask.run();
                queuedTasks.remove(i);
                i--;
            }
        }
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

    public String getId() {
        return mapFileLoader.getId();
    }

    public float getHeight() {
        return (((float) Main.VIEWPORT_HEIGHT) / Main.VIEWPORT_WIDTH) * width;
    }

    public float getTime() {
        return TimeUtil.convertFramesToSeconds(frame);
    }

    public float getTotalTime() {
        return TimeUtil.convertFramesToSeconds(totalFrame);
    }
}
