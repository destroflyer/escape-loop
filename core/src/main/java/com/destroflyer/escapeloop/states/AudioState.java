package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.destroflyer.escapeloop.State;

import java.util.HashMap;

public class AudioState extends State {

    private HashMap<String, Music> musics = new HashMap<>();
    private HashMap<String, Sound> sounds = new HashMap<>();
    private Music currentMusic;

    @Override
    public void create() {
        super.create();
        loadMusics("main", "intro");
        loadSounds("action", "alarm", "bounce", "button", "explosion", "jump", "loss", "pickup", "shot", "trigger", "win");
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
            loadSound(name);
        }
    }

    private void loadSound(String name) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("./sounds/" + name + ".mp3"));
        sounds.put(name, sound);
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
        sounds.get(name).play(getVolume());
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
            currentMusic.setVolume(getVolume());
        }
    }

    private float getVolume() {
        return main.getSettingsState().getPreferences().getFloat("musicVolume");
    }

    @Override
    public void dispose() {
        for (Music music : musics.values()) {
            music.dispose();
        }
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
    }
}
