package com.destroflyer.escapeloop.game.loader;

import com.badlogic.gdx.math.Vector2;
import com.destroflyer.escapeloop.game.Cinematic;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.game.PlayMap;
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
import com.destroflyer.escapeloop.states.PlayMapState;

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
        if (map instanceof PlayMap) {
            PlayMap playMap = (PlayMap) map;
            switch (playMap.getMapIndex()) {
                case 0: return new IntroCinematic(playMap);
                case 99: return new OutroCinematic(playMap);
            }
        }
        return null;
    }

    public void loadContent() {
        switch (map.getMapIndex()) {
            case 0:
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        Decoration turbinePiece = new Decoration(5, (y == 2) ? 0 : 1);
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
                if (!(map instanceof PlayMap)) {
                    return;
                }
                PlayMap playMap = (PlayMap) map;
                if (!finalSceneTriggered && (playMap.getPlayer().getBody().getPosition().x > (6 * Map.TILE_SIZE))) {
                    Scientist scientistLeft = scientists.get(1);
                    Scientist scientistRight = scientists.get(0);
                    scientistLeft.setSpeech("<click>", 1f);
                    scientistLeft.setWalkDirection(1);
                    scientistRight.setWalkDirection(1);
                    playMap.queueTask(scientistRight::remove, 0.9f);
                    playMap.queueTask(scientistLeft::remove, 1.2f);

                    Gate gateTop = gates.get(0);
                    gateTop.setOpening(true);

                    Gate gateRight = gates.get(1);
                    gateRight.setOpening(true);
                    playMap.queueTask(() -> gateRight.setOpening(false), 1);

                    playMap.queueTask(() -> {
                        playMap.getPlayer().setSpeech("!", 1f);
                        playMap.queueTask(() -> {
                            playMap.getPlayer().setSpeech("x_x", 4f);
                            playMap.getPlayer().setWalkDirection(0);
                            playMap.setAcceptsInputs(false);
                            playMap.queueTask(() -> {
                                for (int i = 0; i < 3; i++) {
                                    Enemy enemy = new Enemy(10, 0, false);
                                    playMap.addObject(enemy);
                                    enemy.getBody().setTransform(new Vector2((10 + (i * 6)) * Map.TILE_SIZE, 19 * Map.TILE_SIZE), 0);
                                    playMap.queueTask(() -> {
                                        enemy.setSpeech("Next task:", 2f);
                                        playMap.queueTask(() -> enemy.setSpeech("ELIMINATE!", 2f), 2.5f);
                                    }, 2);
                                }
                                playMap.queueTask(() -> {
                                    playMap.getPlayer().setSpeech("!", 1f);
                                    float wiggleDuration = 0.1f;
                                    float nextWiggleDelay = 0.5f;
                                    int nextWiggleDirection = -1 * playMap.getPlayer().getViewDirection();
                                    for (int i = 0; i < 6; i++) {
                                        int _nextWiggleDirection = nextWiggleDirection;
                                        playMap.queueTask(() -> playMap.getPlayer().setWalkDirection(_nextWiggleDirection), nextWiggleDelay);
                                        nextWiggleDelay += wiggleDuration;
                                        nextWiggleDirection *= -1;
                                    }
                                    playMap.queueTask(() -> {
                                        playMap.getPlayer().setWalkDirection(0);
                                        playMap.queueTask(() -> {
                                            playMap.getPlayer().setSpeech("!", 1f);
                                            playMap.queueTask(() -> {
                                                playMap.getPlayer().setSpeech("<nervous click>", 1.5f);
                                                playMap.queueTask(() -> {
                                                    playMap.getPlayer().setSpeech("<WARNING!>", 2f);
                                                    playMap.queueTask(() -> {
                                                        playMap.getPlayer().setSpeech("<NOT FULLY CHARGED!>", 2.5f);
                                                        playMap.queueTask(() -> {
                                                            playMap.getPlayer().setSpeech("<TIME MACHINE UNSTABLE!>", 3f);
                                                            playMap.queueTask(() -> {
                                                                playMap.getPlayer().setSpeech("<OUTCOME UNKNOWN!>", 3f);
                                                                playMap.queueTask(() -> {
                                                                    playMap.getPlayer().setSpeech("<CONTINUE?>", 2.5f);
                                                                    playMap.queueTask(() -> {
                                                                        playMap.getPlayer().setSpeech("<click>");
                                                                        playMap.queueTask(() -> playMap.getMapState().switchToState(new PlayMapState(0)), 0.5f);
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
