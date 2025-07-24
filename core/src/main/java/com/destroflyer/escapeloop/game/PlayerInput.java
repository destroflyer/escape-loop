package com.destroflyer.escapeloop.game;

import com.destroflyer.escapeloop.game.objects.Player;

import lombok.Getter;
import lombok.Setter;

public abstract class PlayerInput {

    @Getter
    @Setter
    private float time;

    public abstract void apply(Player player);
}
