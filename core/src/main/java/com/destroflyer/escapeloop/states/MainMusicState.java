package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.destroflyer.escapeloop.State;

public class MainMusicState extends State {

    private Music music;

    @Override
    public void create() {
        super.create();
        music = Gdx.audio.newMusic(Gdx.files.internal("./music/main.mp3"));
        music.setLooping(true);
        music.setVolume(0.2f);
        music.play();
    }

    public void dispose() {
        music.dispose();
    }
}
