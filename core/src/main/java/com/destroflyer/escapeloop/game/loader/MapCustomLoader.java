package com.destroflyer.escapeloop.game.loader;

import com.badlogic.gdx.math.Vector2;
import com.destroflyer.escapeloop.game.Cinematic;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.game.cinematics.IntroCinematic;
import com.destroflyer.escapeloop.game.cinematics.OutroCinematic;
import com.destroflyer.escapeloop.game.objects.Decoration;
import com.destroflyer.escapeloop.game.objects.Enemy;
import com.destroflyer.escapeloop.game.objects.Gate;
import com.destroflyer.escapeloop.game.objects.Item;
import com.destroflyer.escapeloop.game.objects.Scientist;
import com.destroflyer.escapeloop.game.objects.items.HeavyItem;
import com.destroflyer.escapeloop.game.objects.items.KnockbackItem;
import com.destroflyer.escapeloop.game.objects.items.SwapItem;
import com.destroflyer.escapeloop.states.MapState;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;

public class MapCustomLoader {

    public MapCustomLoader(Map map) {
        this.map = map;
        reset();
    }
    private Map map;
    private ArrayList<Gate> gates = new ArrayList<>();
    private ArrayList<Scientist> scientists = new ArrayList<>();
    private boolean finalSceneTriggered;

    public void reset() {
        gates.clear();
        scientists.clear();
        finalSceneTriggered = false;
    }

    public Cinematic getCinematic() {
        switch (map.getMapIndex()) {
            case 0: return new IntroCinematic(map);
            case 99: return new OutroCinematic(map);
        }
        return null;
    }

    public void loadContent() {
        switch (map.getMapIndex()) {
            case 0:
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        Decoration turbinePiece = new Decoration(5, (y == 2) ? 5 : 6);
                        map.addObject(turbinePiece);
                        turbinePiece.getBody().setTransform(new Vector2((2.5f + x) * Map.TILE_SIZE, (2.5f + y) * Map.TILE_SIZE), 0);
                    }
                }
                break;
            case 99:
                Supplier<Item>[] itemSuppliers = new Supplier[] {
                    HeavyItem::new,
                    KnockbackItem::new,
                    SwapItem::new,
                };
                int itemIndex = 0;
                Random random = new Random(42);
                for (int x = 7; x < 26; x++) {
                    for (int y = 13; y < 19; y++) {
                        for (int i = 0; i < 4; i++) {
                            Item item = itemSuppliers[itemIndex++ % itemSuppliers.length].get();
                            map.addObject(item);
                            item.setBlocking();
                            item.getBody().setTransform(new Vector2((x + random.nextFloat()) * Map.TILE_SIZE, (y + random.nextFloat()) * Map.TILE_SIZE), 0);
                        }
                    }
                }
                break;
        }
        for (MapObject mapObject : map.getObjects()) {
            if (mapObject instanceof Gate) {
                gates.add((Gate) mapObject);
            } else if (mapObject instanceof Scientist) {
                scientists.add((Scientist) mapObject);
            }
        }
    }

    public void update() {
        switch (map.getMapIndex()) {
            case 99:
                if (!finalSceneTriggered && (map.getPlayer().getBody().getPosition().x > (6 * Map.TILE_SIZE))) {
                    Scientist scientistLeft = scientists.get(1);
                    Scientist scientistRight = scientists.get(0);
                    scientistLeft.setSpeech("<click>", 1f);
                    scientistLeft.setWalkDirection(1);
                    scientistRight.setWalkDirection(1);
                    map.queueTask(() -> map.removeObject(scientistRight), 0.9f);
                    map.queueTask(() -> map.removeObject(scientistLeft), 1.2f);

                    Gate gateTop = gates.get(0);
                    gateTop.setOpening(true);

                    Gate gateRight = gates.get(1);
                    gateRight.setOpening(true);
                    map.queueTask(() -> gateRight.setOpening(false), 1);

                    map.queueTask(() -> {
                        map.getPlayer().setSpeech("!", 1f);
                        map.queueTask(() -> {
                            map.getPlayer().setSpeech("x_x", 4f);
                            map.getPlayer().setWalkDirection(0);
                            map.setAcceptsInputs(false);
                            map.queueTask(() -> {
                                for (int i = 0; i < 3; i++) {
                                    Enemy enemy = new Enemy(10, 0, false);
                                    map.addObject(enemy);
                                    enemy.getBody().setTransform(new Vector2((10 + (i * 6)) * Map.TILE_SIZE, 19 * Map.TILE_SIZE), 0);
                                    map.queueTask(() -> {
                                        enemy.setSpeech("Next task:", 2f);
                                        map.queueTask(() -> enemy.setSpeech("ELIMINATE!", 2f), 2.5f);
                                    }, 2);
                                }
                                map.queueTask(() -> {
                                    map.getPlayer().setSpeech("!", 1f);
                                    float wiggleDuration = 0.1f;
                                    float nextWiggleDelay = 0.5f;
                                    int nextWiggleDirection = -1 * map.getPlayer().getViewDirection();
                                    for (int i = 0; i < 6; i++) {
                                        int _nextWiggleDirection = nextWiggleDirection;
                                        map.queueTask(() -> map.getPlayer().setWalkDirection(_nextWiggleDirection), nextWiggleDelay);
                                        nextWiggleDelay += wiggleDuration;
                                        nextWiggleDirection *= -1;
                                    }
                                    map.queueTask(() -> {
                                        map.getPlayer().setWalkDirection(0);
                                        map.queueTask(() -> {
                                            map.getPlayer().setSpeech("!", 1f);
                                            map.queueTask(() -> {
                                                map.getPlayer().setSpeech("<nervous click>", 1.5f);
                                                map.queueTask(() -> {
                                                    map.getPlayer().setSpeech("<WARNING!>", 2f);
                                                    map.queueTask(() -> {
                                                        map.getPlayer().setSpeech("<NOT FULLY CHARGED!>", 2.5f);
                                                        map.queueTask(() -> {
                                                            map.getPlayer().setSpeech("<TIME MACHINE UNSTABLE!>", 3f);
                                                            map.queueTask(() -> {
                                                                map.getPlayer().setSpeech("<OUTCOME UNKNOWN!>", 3f);
                                                                map.queueTask(() -> {
                                                                    map.getPlayer().setSpeech("<CONTINUE?>", 2.5f);
                                                                    map.queueTask(() -> {
                                                                        map.getPlayer().setSpeech("<click>");
                                                                        map.queueTask(() -> map.getMapState().switchToState(new MapState(0)), 0.5f);
                                                                    }, 6);
                                                                }, 4);
                                                            }, 4);
                                                        }, 3.5f);
                                                    }, 3);
                                                }, 2.5f);
                                            }, 2.5f);
                                        }, 2);
                                    }, nextWiggleDelay);
                                }, 7);
                            }, 3.5f);
                        }, 2.5f);
                    }, 1);

                    finalSceneTriggered = true;
                }
                break;
        }
    }
}
