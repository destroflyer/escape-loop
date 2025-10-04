package com.destroflyer.escapeloop.states.models;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SetHighscoreDto {
    private String context;
    private String evaluation;
    private long score;
    private String metadata;
}
