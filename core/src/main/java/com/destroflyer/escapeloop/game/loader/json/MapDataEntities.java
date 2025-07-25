package com.destroflyer.escapeloop.game.loader.json;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MapDataEntities {
    private ArrayList<MapDataEntity> Start;
    private ArrayList<MapDataEntity> Finish;
    private ArrayList<MapDataEntity> Enemy;
    private ArrayList<MapDataEntity> Item;
    private ArrayList<MapDataEntity> Bouncer;
    private ArrayList<MapDataEntity> Toggle_Trigger;
    private ArrayList<MapDataEntity> Pressure_Trigger;
    private ArrayList<MapDataEntity> Gate;
    private ArrayList<MapDataEntity> Text;
}
