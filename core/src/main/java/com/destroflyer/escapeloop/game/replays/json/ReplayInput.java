package com.destroflyer.escapeloop.game.replays.json;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ReplayInput {
    private String type;
    private Integer horizontalDirection;
    private Integer verticalDirection;
}
