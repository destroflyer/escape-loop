package com.destroflyer.escapeloop.game.replays.json;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class ReplayPast {
    private ArrayList<ReplayFrame> frames;
}
