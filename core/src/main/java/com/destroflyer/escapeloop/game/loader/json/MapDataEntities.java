package com.destroflyer.escapeloop.game.loader.json;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MapDataEntities {
    private ArrayList<MapDataEntity> Player;
    private ArrayList<MapDataEntity> Finish;
}
