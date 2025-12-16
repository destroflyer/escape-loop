package com.destroflyer.escapeloop.game.replays;

import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.PlayMap;
import com.destroflyer.escapeloop.game.PlayerInput;
import com.destroflyer.escapeloop.game.PlayerPast;
import com.destroflyer.escapeloop.game.PlayerPastFrame;
import com.destroflyer.escapeloop.game.inputs.ActionInput;
import com.destroflyer.escapeloop.game.inputs.JumpInput;
import com.destroflyer.escapeloop.game.inputs.SetVerticalDirectionInput;
import com.destroflyer.escapeloop.game.inputs.SetWalkDirectionInput;
import com.destroflyer.escapeloop.game.replays.json.Replay;
import com.destroflyer.escapeloop.game.replays.json.ReplayFrame;
import com.destroflyer.escapeloop.game.replays.json.ReplayInput;
import com.destroflyer.escapeloop.game.replays.json.ReplayMetadata;
import com.destroflyer.escapeloop.game.replays.json.ReplayPast;

import java.util.ArrayList;

public class ReplayConverter {

    public static Replay convertToReplay(PlayMap map, float tpf) {
        ArrayList<ReplayPast> replayPasts = new ArrayList<>();
        for (PlayerPast playerPast : map.getPlayerPasts()) {
            replayPasts.add(convertToReplayPast(playerPast));
        }
        replayPasts.add(convertToReplayPast(map.createCurrentPlayerPast()));
        return new Replay(replayPasts, new ReplayMetadata(Main.FPS, tpf));
    }

    private static ReplayPast convertToReplayPast(PlayerPast playerPast) {
        ArrayList<ReplayFrame> replayFrames = new ArrayList<>();
        for (PlayerPastFrame pastFrame : playerPast.getFrames()) {
            ArrayList<ReplayInput> replayInputs = new ArrayList<>();
            for (PlayerInput input : pastFrame.getInputs()) {
                replayInputs.add(convertToReplayInput(input));
            }
            if (replayInputs.size() > 0) {
                replayFrames.add(new ReplayFrame(pastFrame.getFrame(), replayInputs));
            }
        }
        return new ReplayPast(replayFrames);
    }

    private static ReplayInput convertToReplayInput(PlayerInput input) {
        ReplayInput replayInput = new ReplayInput();
        if (input instanceof ActionInput) {
            replayInput.setType("action");
        } else if (input instanceof JumpInput) {
            replayInput.setType("jump");
        } else if (input instanceof SetWalkDirectionInput) {
            SetWalkDirectionInput setWalkDirectionInput = (SetWalkDirectionInput) input;
            replayInput.setType("setHorizontalDirection");
            replayInput.setHorizontalDirection(setWalkDirectionInput.getWalkDirection());
        } else if (input instanceof SetVerticalDirectionInput) {
            SetVerticalDirectionInput setVerticalDirectionInput = (SetVerticalDirectionInput) input;
            replayInput.setType("setVerticalDirection");
            replayInput.setVerticalDirection(setVerticalDirectionInput.getVerticalDirection());
        } else {
            throw new IllegalArgumentException("Unsupported input: " + input);
        }
        return replayInput;
    }

    public static ArrayList<PlayerPast> convertFromReplay(Replay replay) {
        ArrayList<PlayerPast> playerPasts = new ArrayList<>();
        for (ReplayPast replayPast : replay.getPasts()) {
            ArrayList<PlayerPastFrame> pastFrames = new ArrayList<>();
            for (ReplayFrame replayFrame : replayPast.getFrames()) {
                ArrayList<PlayerInput> inputs = new ArrayList<>();
                for (ReplayInput replayInput : replayFrame.getInputs()) {
                    inputs.add(convertFromReplayInput(replayInput));
                }
                pastFrames.add(new PlayerPastFrame(replayFrame.getFrame(), inputs));
            }
            playerPasts.add(new PlayerPast(pastFrames));
        }
        return playerPasts;
    }

    private static PlayerInput convertFromReplayInput(ReplayInput replayInput) {
        switch (replayInput.getType()) {
            case "action": return new ActionInput();
            case "jump": return new JumpInput();
            case "setHorizontalDirection": return new SetWalkDirectionInput(replayInput.getHorizontalDirection());
            case "setVerticalDirection": return new SetVerticalDirectionInput(replayInput.getVerticalDirection());
            default: throw new IllegalArgumentException("Unsupported input type: " + replayInput.getType());
        }
    }
}
