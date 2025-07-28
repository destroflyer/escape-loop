package com.destroflyer.escapeloop.game.inputs;

import com.destroflyer.escapeloop.game.PlayerInput;
import com.destroflyer.escapeloop.game.objects.Player;

public class ActionInput extends PlayerInput {

    @Override
    public void apply(Player player) {
        player.action();
    }
}
