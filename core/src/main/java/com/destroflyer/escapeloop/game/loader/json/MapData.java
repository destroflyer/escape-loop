package com.destroflyer.escapeloop.game.loader.json;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MapData {
    private String uniqueIdentifer;
    private int width;
    private int height;
    private MapDataCustomFields customFields;
    private MapDataEntities entities;
}
