package com.destroflyer.escapeloop.game.loader.json;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MapDataEntity {
    private String iid;
    private int x;
    private int y;
    private int width;
    private int height;
    private MapDataEntityCustomFields customFields;
}
