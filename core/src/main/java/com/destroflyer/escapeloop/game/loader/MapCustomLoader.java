package com.destroflyer.escapeloop.game.loader;

import com.badlogic.gdx.math.Vector2;
import com.destroflyer.escapeloop.game.Cinematic;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.cinematics.IntroCinematic;
import com.destroflyer.escapeloop.game.cinematics.OutroCinematic;
import com.destroflyer.escapeloop.game.objects.Decoration;
import com.destroflyer.escapeloop.game.objects.Gate;
import com.destroflyer.escapeloop.game.objects.Item;
import com.destroflyer.escapeloop.game.objects.items.HeavyItem;
import com.destroflyer.escapeloop.game.objects.items.KnockbackItem;
import com.destroflyer.escapeloop.game.objects.items.SwapItem;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MapCustomLoader {

    public MapCustomLoader(Map map) {
        this.map = map;
        reset();
    }
    private Map map;
    private List<Gate> gates;
    private Float finalSceneTimeSinceStart;
    private boolean finalSceneEndTriggered;

    public void reset() {
        finalSceneTimeSinceStart = null;
        finalSceneEndTriggered = false;
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
        gates = map.getObjects().stream()
            .filter(mapObject -> mapObject instanceof Gate)
            .map(mapObject -> (Gate) mapObject).collect(Collectors.toList());
    }

    public void update(float tpf) {
        switch (map.getMapIndex()) {
            case 99:
                if (finalSceneTimeSinceStart == null) {
                    if (map.getPlayer().getBody().getPosition().x > (6 * Map.TILE_SIZE)) {
                        Gate gateTop = gates.get(0);
                        gateTop.setOpening(true);
                        finalSceneTimeSinceStart = 0f;
                    }
                } else if (!finalSceneEndTriggered) {
                    finalSceneTimeSinceStart += tpf;
                    if (finalSceneTimeSinceStart < 0.75f) {
                        map.getPlayer().setSpeech(null);
                    } else if (finalSceneTimeSinceStart < 1.5f) {
                        map.getPlayer().setSpeech("!");
                    } else if (finalSceneTimeSinceStart < 3.5f) {
                        map.getPlayer().setSpeech(null);
                    } else {
                        map.getPlayer().setSpeech("x_x", 4f);
                        map.getPlayer().setWalkDirection(0);
                        map.setAcceptsInputs(false);
                        finalSceneEndTriggered = true;
                    }
                }
                break;
        }
    }
}
