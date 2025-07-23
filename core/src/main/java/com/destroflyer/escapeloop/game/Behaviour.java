package com.destroflyer.escapeloop.game;

import lombok.Setter;

public abstract class Behaviour<T extends MapObject> {

    @Setter
    protected T mapObject;

    public abstract void update(float tpf);
}
