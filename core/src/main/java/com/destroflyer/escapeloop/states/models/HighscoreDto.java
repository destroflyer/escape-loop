package com.destroflyer.escapeloop.states.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class HighscoreDto {
    private String context;
    private UserDto user;
    private int score;
}
