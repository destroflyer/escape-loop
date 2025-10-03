package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.audio.Sound;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SoundWithVolume {

    public SoundWithVolume(Sound sound) {
        this(sound, 1);
    }
    private Sound sound;
    private float volume;
}
