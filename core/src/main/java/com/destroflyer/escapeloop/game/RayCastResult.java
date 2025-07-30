package com.destroflyer.escapeloop.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RayCastResult {
    private MapObject mapObject;
    private Fixture fixture;
    private Vector2 point;
}
