package com.destroflyer.escapeloop.game.loader.json;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MapDataEntityCustomFields {
    // Enemy
    private boolean autoShoot;
    private float shootCooldown;
    private MapDataEntityCustomFieldPosition patrolHorizontal;
    private MapDataEntityCustomFieldPosition patrolVertical;
    // Item
    private String item;
    // Toggle trigger + Pressure trigger
    private MapDataEntityCustomFieldEntity gate;
    // Toggle trigger
    private String autoRevertDuration;
    // Text
    private String text;
}
