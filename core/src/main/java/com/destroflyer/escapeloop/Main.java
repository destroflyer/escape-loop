package com.destroflyer.escapeloop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.destroflyer.escapeloop.states.AchievementsState;
import com.destroflyer.escapeloop.states.CreditsState;
import com.destroflyer.escapeloop.states.DestrostudiosState;
import com.destroflyer.escapeloop.states.HttpState;
import com.destroflyer.escapeloop.states.MainMenuState;
import com.destroflyer.escapeloop.states.AudioState;
import com.destroflyer.escapeloop.states.MapSelectionState;
import com.destroflyer.escapeloop.states.MapsState;
import com.destroflyer.escapeloop.states.SettingsState;
import com.destroflyer.escapeloop.states.SkinsState;
import com.destroflyer.escapeloop.states.models.Account;
import com.destroflyer.escapeloop.util.AuthTokenUtil;

import java.util.ArrayList;

import lombok.Getter;

public class Main extends ApplicationAdapter {

    public Main(String authToken) {
        this.authToken = authToken;
    }
    public static final int VIEWPORT_WIDTH = 1280;
    public static final int VIEWPORT_HEIGHT = 720;
    public static final int FPS = 166;
    @Getter
    private String authToken;
    @Getter
    private Account account;
    @Getter
    private StretchViewport viewport;
    private InputMultiplexer inputMultiplexer;
    @Getter
    private Skin skinSmall;
    @Getter
    private Skin skinLarge;
    private ArrayList<State> states;
    @Getter
    private SettingsState settingsState;
    @Getter
    private AudioState audioState;
    @Getter
    private MapsState mapsState;
    @Getter
    private SkinsState skinsState;
    @Getter
    private MainMenuState mainMenuState;
    @Getter
    private MapSelectionState mapSelectionState;
    @Getter
    private AchievementsState achievementsState;
    @Getter
    private CreditsState creditsState;
    @Getter
    private HttpState httpState;
    @Getter
    private DestrostudiosState destrostudiosState;
    @Getter
    private float time;

    @Override
    public void create() {
        account = AuthTokenUtil.getAccount(authToken);

        viewport = new StretchViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);

        skinSmall = new Skin(Gdx.files.internal("skins/small/uiskin.json"));
        skinLarge = new Skin(Gdx.files.internal("skins/large/uiskin.json"));

        states = new ArrayList<>();

        settingsState = new SettingsState();

        audioState = new AudioState();
        addState(audioState);

        mapsState = new MapsState();
        addState(mapsState);

        skinsState = new SkinsState();
        addState(skinsState);

        mainMenuState = new MainMenuState();
        addState(mainMenuState);

        mapSelectionState = new MapSelectionState();

        achievementsState = new AchievementsState();

        creditsState = new CreditsState();

        httpState = new HttpState();
        addState(httpState);

        destrostudiosState = new DestrostudiosState();
        addState(destrostudiosState);
    }

    public void openSettings(Runnable back) {
        settingsState.setBack(back);
        addState(settingsState);
    }

    public void addState(State state) {
        state.onAdd(this);
        notifyStateOfResize(state);
        states.add(state);
        for (InputProcessor inputProcessor : state.getInputProcessors()) {
            inputMultiplexer.addProcessor(0, inputProcessor);
        }
    }

    public void removeState(State state) {
        states.remove(state);
        for (InputProcessor inputProcessor : state.getInputProcessors()) {
            inputMultiplexer.removeProcessor(inputProcessor);
        }
        for (State childState : state.getChildStates()) {
            removeState(childState);
        }
    }

    @Override
    public void render() {
        float tpf = Gdx.graphics.getDeltaTime();
        time += tpf;
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1);
        for (State state : states.toArray(new State[0])) {
            state.update(tpf);
            state.render();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height, true);
        for (State state : states) {
            notifyStateOfResize(state);
        }
    }

    private void notifyStateOfResize(State state) {
        state.resize(viewport.getCamera().combined);
    }

    @Override
    public void dispose() {
        for (State state : states) {
            state.dispose();
        }
    }
}
