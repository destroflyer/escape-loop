package com.destroflyer.escapeloop.game.cinematics;

import com.badlogic.gdx.math.Vector2;
import com.destroflyer.escapeloop.game.Cinematic;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.objects.Decoration;
import com.destroflyer.escapeloop.game.objects.Player;
import com.destroflyer.escapeloop.game.objects.Scientist;

import java.util.List;
import java.util.stream.Collectors;

public class IntroCinematic extends Cinematic {

    public IntroCinematic(Map map) {
        super(map);

        Scientist scientistLeft = new Scientist();
        Scientist scientistRight = new Scientist();
        map.addObject(scientistLeft);
        map.addObject(scientistRight);
        scientistLeft.getBody().setTransform(getTileCenter(12, GROUND_TILE_Y), 0);
        scientistRight.getBody().setTransform(getTileCenter(15, GROUND_TILE_Y), 0);
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

        Player player = map.getPlayer();
        player.getBody().setTransform(getTileCenter(6, GROUND_TILE_Y), 0);

        Decoration box = new Decoration(1, 8);
        map.addObject(box);
        box.getBody().setTransform(getTileCenter(7, GROUND_TILE_Y), 0);

        time = 0;
        turbinePieces = map.getObjects().stream()
            .filter(mapObject -> (mapObject instanceof Decoration) && (mapObject != box))
            .map(mapObject -> (Decoration) mapObject)
            .collect(Collectors.toList());
        int turbinesCount = 3;
        float walkDurationForFarthestTurbine = 0.75f;
        int i = 0;
        for (Decoration turbinePiece : turbinePieces) {
            turbinePiece.setVisible(false);
            int turbineIndex = (i / turbinesCount);
            float walkDuration = walkDurationForFarthestTurbine * (((float) (turbinesCount - turbineIndex)) / turbinesCount);
            boolean isLastPiece = (i == (turbinePieces.size() - 1));
            add(time, () -> {
                player.setWalkDirection(-1);
                if (isLastPiece) {
                    box.remove();
                }
            });
            time += walkDuration;
            add(time, () -> {
                turbinePiece.setVisible(true);
                player.setWalkDirection(1);
            });
            time += walkDuration;
            i++;
        }
        add(time, () -> {
            player.setWalkDirection(0);
            player.setSpeech("Finished", 1f);
        });
        time += 2;
        add(time, () -> player.setSpeech("...", 1f));
        time += 2;
        add(time, () -> player.setSpeech("!", 0.5f));
        time += 0.5f;
        add(time, () -> player.setWalkDirection(1));
        time += 0.7f;
        add(time, () -> player.setWalkDirection(0));
        time += 0.5f;
        add(time, () -> player.setSpeech("...", 1f));
        time += 2;
        add(time, () -> player.setWalkDirection(1));
        time += 0.3f;

        duration = time;
    }
    private static final int GROUND_TILE_Y = 2;
    private List<Decoration> turbinePieces;

    @Override
    public void finish() {
        super.finish();
        for (Decoration turbine : turbinePieces) {
            turbine.setVisible(true);
        }
    }

    private Vector2 getTileCenter(int tileX, int tileY) {
        return new Vector2((tileX + 0.5f) * Map.TILE_SIZE, (tileY + 0.5f) * Map.TILE_SIZE);
    }
}
