package com.destroflyer.escapeloop.game.inputs;

import com.destroflyer.escapeloop.game.PlayerInput;
import com.destroflyer.escapeloop.game.objects.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SetVerticalDirectionInput extends PlayerInput {

    private int verticalDirection;

    @Override
    public void apply(Player player) {
        player.setVerticalDirection(verticalDirection);
    }
}
