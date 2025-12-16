package com.destroflyer.escapeloop.game;

import com.destroflyer.escapeloop.game.objects.Player;
import com.destroflyer.escapeloop.states.AudioState;
import com.destroflyer.escapeloop.states.MapState;
import com.destroflyer.escapeloop.states.SettingsState;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class PlayMap extends Map {

    public PlayMap(int mapIndex, MapState<?, ?> mapState, SettingsState settingsState, AudioState audioState) {
        super(mapIndex, mapState, settingsState, audioState);
    }
    @Getter
    protected int maximumPlayerPasts;
    @Getter
    private Player player;
    private ArrayList<PlayerInput> currentPlayerCurrentFrameInputs = new ArrayList<>();
    private ArrayList<PlayerPastFrame> currentPlayerFrames = new ArrayList<>();
    @Setter
    private boolean acceptsInputs;

    public void tryStartNextPlayer() {
        if (playerPasts.size() < maximumPlayerPasts) {
            playerPasts.add(createCurrentPlayerPast());
            start();
            audioState.playSound("time_machine");
        }
    }

    public PlayerPast createCurrentPlayerPast() {
        return new PlayerPast(new ArrayList<>(currentPlayerFrames));
    }

    public void respawnCurrentPlayer() {
        if (playerPasts.isEmpty()) {
            reset();
        } else {
            start();
        }
        audioState.playSound("loss");
    }

    @Override
    public void reset() {
        super.reset();
        acceptsInputs = true;
    }

    @Override
    protected void loadContent() {
        super.loadContent();
        maximumPlayerPasts = mapFileLoader.getMaximumPlayerPasts();

        player = new Player();
        addObject(player);
        player.getBody().setTransform(mapFileLoader.getStartPosition(), 0);
        currentPlayerCurrentFrameInputs.clear();
        currentPlayerFrames.clear();
    }

    @Override
    public void update() {
        super.update();
        if (!isPlayerAlive(player)) {
            respawnCurrentPlayer();
        }
    }

    @Override
    protected void updatePlayers() {
        super.updatePlayers();
        currentPlayerFrames.add(new PlayerPastFrame(frame, new ArrayList<>(currentPlayerCurrentFrameInputs), player.getBody().getPosition().cpy()));
        currentPlayerCurrentFrameInputs.clear();
    }

    public void applyInput(PlayerInput input) {
        if (acceptsInputs) {
            input.apply(player);
            currentPlayerCurrentFrameInputs.add(input);
        }
    }
}
