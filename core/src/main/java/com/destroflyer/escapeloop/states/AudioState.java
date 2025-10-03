package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.destroflyer.escapeloop.State;

import java.util.HashMap;

public class AudioState extends State {

    private HashMap<String, Music> musics = new HashMap<>();
    private HashMap<String, SoundWithVolume> sounds = new HashMap<>();
    private Music currentMusic;

    @Override
    public void create() {
        super.create();
        loadMusics("main", "intro");
        loadSounds("action", "alarm", "button", "explosion", "jump", "loss", "pickup", "shot", "win");
        loadSound("bounce", 2);
        loadSound("time_machine", 1.5f);
        loadSound("trigger", 3);
        playMusic("main");
    }

    private void loadMusics(String... names) {
        for (String name : names) {
            loadMusic(name);
        }
    }

    private void loadMusic(String name) {
        Music music = Gdx.audio.newMusic(Gdx.files.internal("./music/" + name + ".mp3"));
        musics.put(name, music);
    }

    private void loadSounds(String... names) {
        for (String name : names) {
            loadSound(name, 1);
        }
    }

    private void loadSound(String name, float baseVolume) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("./sounds/" + name + ".mp3"));
        // Ensure everything is preloaded+prepared as much as possible
        sound.play(0);
        sounds.put(name, new SoundWithVolume(sound, baseVolume));
    }

    public void playMusic(String name) {
        playMusic(name, true);
    }

    public void playMusic(String name, boolean looping) {
        stopMusic();
        // Avoids a loud noise when immediately switching from one music to another one
        Gdx.app.postRunnable(() -> {
            currentMusic = musics.get(name);
            currentMusic.setLooping(looping);
            currentMusic.setOnCompletionListener(music -> {
                if (!looping) {
                    stopMusic();
                }
            });
            currentMusic.play();
        });
    }

    public void playSound(String name) {
        SoundWithVolume sound = sounds.get(name);
        sound.getSound().play(getVolume("volumeMaster") * getVolume("volumeSound") * sound.getVolume());
    }

    public void pauseMusic() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    public void resumeMusic() {
        if (currentMusic != null) {
            currentMusic.play();
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (currentMusic != null) {
            currentMusic.setVolume(getVolume("volumeMaster") * getVolume("volumeMusic"));
        }
    }

    private float getVolume(String key) {
        return main.getSettingsState().getPreferences().getFloat(key);
    }

    @Override
    public void dispose() {
        for (Music music : musics.values()) {
            music.dispose();
        }
        for (SoundWithVolume sound : sounds.values()) {
            sound.getSound().dispose();
        }
    }
}
