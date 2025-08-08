package com.destroflyer.escapeloop.game.cinematics;

import com.badlogic.gdx.math.Vector2;
import com.destroflyer.escapeloop.game.Cinematic;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.objects.Scientist;

public class IntroCinematic extends Cinematic {

    public IntroCinematic(Map map) {
        super(map);

        Scientist scientistLeft = new Scientist();
        Scientist scientistRight = new Scientist();
        map.addObject(scientistLeft);
        map.addObject(scientistRight);
        scientistLeft.getBody().setTransform(new Vector2(6.25f, 1.25f), 0);
        scientistRight.getBody().setTransform(new Vector2(7.75f, 1.25f), 0);
        scientistRight.setViewDirection(-1);

        float time = 0.5f;
        add(time, () -> scientistLeft.setSpeech("Amazing!", 1f));
        time += 1;
        add(time, () -> scientistRight.setSpeech("Yes yes", 1f));
        time += 1;
        add(time, () -> {
            scientistLeft.setWalkDirection(1);
            scientistRight.setWalkDirection(1);
            scientistRight.applyVerticalImpulse(1.35f);
        });
        time += 0.25f;
        add(time, () -> scientistLeft.applyVerticalImpulse(1.3f));
        time += 1.4f;
        add(time, scientistRight::remove);
        time += 0.73f;
        add(time, scientistLeft::remove);

        duration = time;
    }
}
