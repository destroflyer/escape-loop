package com.destroflyer.escapeloop.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
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
    @Getter
    private World world;
    @Getter
    private Player player;
    private ArrayList<PlayerInput> currentPlayerInputs = new ArrayList<>();
    private ArrayList<PlayerPast> playerPasts = new ArrayList<>();
    @Getter
    private boolean finished;

    public void startNextRun() {
        playerPasts.add(new PlayerPast(new ArrayList<>(currentPlayerInputs)));
        reset();
    }

    public void onDeath() {
        playerPasts.clear();
        reset();
    }

    private void reset() {
        time = 0;
        objects.clear();
        disposeWorldContent();
        finished = false;

        width = mapLoader.getWidth();
        mapLoader.loadObjects();
        Vector2 startPosition = mapLoader.getStartPosition();

        player = new Player();
        addObject(player);
        player.getBody().setTransform(startPosition, 0);
        currentPlayerInputs.clear();

        for (PlayerPast playerPast : playerPasts) {
            playerPast.reset();
            addObject(playerPast.getPlayer());
            playerPast.getPlayer().getBody().setTransform(startPosition, 0);
        }
    }

    private void disposeWorldContent() {
        Array<Joint> joints = new Array<>();
        world.getJoints(joints);
        for (Joint joint : joints) {
            world.destroyJoint(joint);
        }
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        for (Body body : bodies) {
            world.destroyBody(body);
        }
    }

    public void addObject(MapObject mapObject) {
        mapObject.setMap(this);
        mapObject.createBody();
        objects.add(mapObject);
    }

    public void update(float tpf) {
        time += tpf;
        for (PlayerPast playerPast : playerPasts) {
            playerPast.applyInputs(time);
        }
        world.step(tpf, VELOCITY_ITERATIONS, POSITIONS_ITERATIONS);
        for (MapObject mapObject : objects) {
            mapObject.update(tpf);
        }
        if (player.getBody().getPosition().y < -1) {
            onDeath();
        }
    }

    public void applyInput(PlayerInput input) {
        input.setTime(time);
        input.apply(player);
        currentPlayerInputs.add(input);
    }

    public void onFinish() {
        finished = true;
    }

    public MapObject getMapObject(Body body) {
        return objects.stream().filter(mapObject -> mapObject.getBody() == body).findAny().orElse(null);
    }
}
