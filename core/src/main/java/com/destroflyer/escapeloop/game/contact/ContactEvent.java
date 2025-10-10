package com.destroflyer.escapeloop.game.contact;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.destroflyer.escapeloop.game.MapObject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ContactEvent {
    private boolean beginOrEnd;
    private MapObject mapObject1;
    private MapObject mapObject2;
    private Fixture fixture1;
    private Fixture fixture2;
}
