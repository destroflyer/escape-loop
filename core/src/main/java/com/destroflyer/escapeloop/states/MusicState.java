package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.destroflyer.escapeloop.State;

import java.util.HashMap;

public class MusicState extends State {

    private HashMap<String, Music> musics = new HashMap<>();
    private Music playingMusic;

    @Override
    public void create() {
        super.create();
        load("main");
        load("intro");
        play("main");
    }

    private void load(String name) {
        Music music = Gdx.audio.newMusic(Gdx.files.internal("./music/" + name + ".mp3"));
        music.setLooping(true);
        musics.put(name, music);
    }

    public void play(String name) {
        stop();
        playingMusic = musics.get(name);
        playingMusic.play();
    }

    public void stop() {
        if (playingMusic != null) {
            playingMusic.stop();
            playingMusic = null;
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (playingMusic != null) {
            playingMusic.setVolume(main.getSettingsState().getPreferences().getFloat("musicVolume"));
        }
    }

    @Override
    public void dispose() {
        for (Music music : musics.values()) {
            music.dispose();
        }
    }
}
