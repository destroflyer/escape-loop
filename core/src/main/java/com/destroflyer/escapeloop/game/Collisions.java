package com.destroflyer.escapeloop.game;

import com.badlogic.gdx.physics.box2d.Fixture;

public class Collisions {

    public static final short PLATFORM = 1;
    public static final short CHARACTER = 2;
    public static final short CHARACTER_FOOT_SENSOR = 4;
    public static final short PLAYER = 8;
    public static final short FINISH = 16;
    public static final short ITEM = 32;
    public static final short TOGGLE_TRIGGER = 64;
    public static final short PRESSURE_TRIGGER = 128;
    public static final short BOUNCER = 256;
    public static final short BULLET = 512;

    public static boolean hasCategory(Fixture fixture, short category) {
        return ((fixture.getFilterData().categoryBits & category) != 0);
    }
}
