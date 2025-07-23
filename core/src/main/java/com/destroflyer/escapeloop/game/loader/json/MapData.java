package com.destroflyer.escapeloop.game.loader.json;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MapData {
    private int width;
    private int height;
    private MapDataEntities entities;
}
