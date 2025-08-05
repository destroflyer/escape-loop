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
        music.play();
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        music.setVolume(main.getSettingsState().getPreferences().getFloat("musicVolume"));
    }

    public void dispose() {
        music.dispose();
    }
}
