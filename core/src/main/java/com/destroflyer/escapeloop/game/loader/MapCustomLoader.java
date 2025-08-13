package com.destroflyer.escapeloop.game.loader;

import com.badlogic.gdx.math.Vector2;
import com.destroflyer.escapeloop.game.Cinematic;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.cinematics.IntroCinematic;
import com.destroflyer.escapeloop.game.objects.Decoration;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MapCustomLoader {

    private Map map;

    public Cinematic getCinematic() {
        switch (map.getMapNumber()) {
            case 1: return new IntroCinematic(map);
        }
        return null;
    }

    public void loadContent() {
        switch (map.getMapNumber()) {
            case 1:
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        Decoration turbinePiece = new Decoration(5, (y == 2) ? 5 : 6);
                        map.addObject(turbinePiece);
                        turbinePiece.getBody().setTransform(new Vector2((2.5f + x) * Map.TILE_SIZE, (2.5f + y) * Map.TILE_SIZE), 0);
                    }
                }
                break;
        }
    }
}
