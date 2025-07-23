package com.destroflyer.escapeloop.game.inputs;

import com.destroflyer.escapeloop.game.Player;
import com.destroflyer.escapeloop.game.PlayerInput;

public class JumpInput extends PlayerInput {

    @Override
    public void apply(Player player) {
        player.jump();
    }
}
