package com.destroflyer.escapeloop.game.loader.json;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MapDataEntityCustomFields {
    // Start
    private boolean visible;
    // Enemy
    private String direction;
    private int hoverTileHeight;
    private boolean autoShoot;
    private float shootCooldown;
    private MapDataEntityCustomFieldPosition patrolHorizontal;
    private MapDataEntityCustomFieldPosition patrolVertical;
    // Item
    private String item;
    // Toggle trigger + Pressure trigger
    private ArrayList<MapDataEntityCustomFieldEntity> gates;
    // Toggle trigger
    private String autoRevertDuration;
    // Text
    private String text;
    private int width;
}
