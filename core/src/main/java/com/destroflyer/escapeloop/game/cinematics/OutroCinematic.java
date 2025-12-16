package com.destroflyer.escapeloop.game.cinematics;

import com.destroflyer.escapeloop.game.Cinematic;
import com.destroflyer.escapeloop.game.PlayMap;
import com.destroflyer.escapeloop.game.objects.Player;
import com.destroflyer.escapeloop.game.objects.Scientist;

import java.util.List;
import java.util.stream.Collectors;

public class OutroCinematic extends Cinematic {

    public OutroCinematic(PlayMap map) {
        super(map);

        Player player = map.getPlayer();
        player.getBody().setTransform(getTileCenter(3, 3), 0);

        List<Scientist> scientists = map.getObjects().stream()
            .filter(mapObject -> mapObject instanceof Scientist)
            .map(mapObject -> (Scientist) mapObject)
            .collect(Collectors.toList());

        Scientist scientistLeft = scientists.get(1);
        Scientist scientistRight = scientists.get(0);

        scientistLeft.setViewDirection(1);
        scientistRight.setViewDirection(1);

        float time = 1;
        add(time, () -> {
            scientistLeft.setViewDirection(-1);
            scientistRight.setViewDirection(-1);
            scientistRight.setSpeech("!", SPEECH_DURATION_SHORT);
        });
        time += SPEECH_DURATION_SHORT + SPEECH_BREAK_LONG;
        add(time, () -> scientistLeft.setSpeech("Amazing", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_LONG;
        add(time, () -> scientistRight.setSpeech("So... persistent", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_LONG;
        add(time, () -> scientistLeft.setSpeech("I'm quite a fan of this one", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_LONG;
        add(time, () -> scientistLeft.setSpeech("But we can't let it escape", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_LONG;
        add(time, () -> scientistRight.setSpeech("There's only one option...", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_SHORT;

        duration = time;
    }
}
