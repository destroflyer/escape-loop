package com.destroflyer.escapeloop.game.inputs;

import com.destroflyer.escapeloop.game.objects.Player;
import com.destroflyer.escapeloop.game.PlayerInput;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SetWalkDirectionInput extends PlayerInput {

    private int walkDirection;

    @Override
    public void apply(Player player) {
        player.setWalkDirection(walkDirection);
    }
}
