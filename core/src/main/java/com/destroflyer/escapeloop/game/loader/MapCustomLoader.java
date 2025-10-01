package com.destroflyer.escapeloop.game.loader;

import com.badlogic.gdx.math.Vector2;
import com.destroflyer.escapeloop.game.Cinematic;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.cinematics.IntroCinematic;
import com.destroflyer.escapeloop.game.cinematics.OutroCinematic;
import com.destroflyer.escapeloop.game.objects.Decoration;
import com.destroflyer.escapeloop.game.objects.Item;
import com.destroflyer.escapeloop.game.objects.items.HeavyItem;
import com.destroflyer.escapeloop.game.objects.items.KnockbackItem;
import com.destroflyer.escapeloop.game.objects.items.SwapItem;

import java.util.Random;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MapCustomLoader {

    private Map map;

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
    }
}
