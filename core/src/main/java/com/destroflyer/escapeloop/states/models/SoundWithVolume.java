package com.destroflyer.escapeloop.states.models;

import com.badlogic.gdx.audio.Sound;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SoundWithVolume {
    private Sound sound;
    private float volume;
}
