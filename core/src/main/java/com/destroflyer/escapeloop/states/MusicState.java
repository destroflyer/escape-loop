package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.destroflyer.escapeloop.State;

import java.util.HashMap;

public class MusicState extends State {

    private HashMap<String, Music> musics = new HashMap<>();
    private Music currentMusic;

    @Override
    public void create() {
        super.create();
        load("main");
        load("intro");
        play("main");
    }

    private void load(String name) {
        Music music = Gdx.audio.newMusic(Gdx.files.internal("./music/" + name + ".mp3"));
        musics.put(name, music);
    }

    public void play(String name) {
        play(name, true);
    }

    public void play(String name, boolean looping) {
        stop();
        currentMusic = musics.get(name);
        currentMusic.setLooping(looping);
        // Avoids a loud noise when immediately switching from one music to another one
        Gdx.app.postRunnable(() -> currentMusic.play());
    }

    public void pause() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    public void resume() {
        if (currentMusic != null) {
            currentMusic.play();
        }
    }

    public void stop() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (currentMusic != null) {
            currentMusic.setVolume(main.getSettingsState().getPreferences().getFloat("musicVolume"));
        }
    }

    @Override
    public void dispose() {
        for (Music music : musics.values()) {
            music.dispose();
        }
    }
}
