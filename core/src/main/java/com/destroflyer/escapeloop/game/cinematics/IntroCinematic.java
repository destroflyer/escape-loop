package com.destroflyer.escapeloop.game.cinematics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.Cinematic;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.objects.Decoration;
import com.destroflyer.escapeloop.game.objects.Player;
import com.destroflyer.escapeloop.game.objects.Scientist;
import com.destroflyer.escapeloop.game.objects.TimeMachine;
import com.destroflyer.escapeloop.util.FloatUtil;

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

        map.getMusicState().play("intro");

        float time = AD_DURATION + ZOOM_OUT_DURATION;
        add(time, () -> map.getMusicState().stop());
        time += 0.5f;
        add(time, () -> scientistLeft.setSpeech("Amazing", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_DURATION_BREAK;
        add(time, () -> scientistRight.setSpeech("Yes, better than last year's", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_DURATION_BREAK;
        add(time, () -> scientistLeft.setSpeech("I'm not a fan of our orbs", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_DURATION_BREAK;
        add(time, () -> scientistLeft.setWalkDirection(-1));
        time += 0.25f;
        add(time, () -> scientistLeft.setWalkDirection(0));
        time += 0.5f;
        add(time, () -> scientistLeft.setSpeech("But they fund our actual project", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_DURATION_BREAK;
        add(time, () -> scientistRight.setWalkDirection(-1));
        time += 0.7f;
        add(time, () -> scientistRight.setWalkDirection(0));
        time += 0.5f;
        add(time, () -> scientistRight.setSpeech("The time machine...", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_DURATION_BREAK;
        add(time, () -> scientistRight.setSpeech("It still has a lot of quirks", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_DURATION_BREAK;
        time += 1;
        add(time, () -> {
            scientistLeft.setViewDirection(1);
            scientistRight.setViewDirection(1);
            scientistRight.setSpeech("!", SPEECH_DURATION_SHORT);
        });
        time += SPEECH_DURATION_SHORT + SPEECH_DURATION_BREAK;
        add(time, () -> scientistRight.setSpeech("Issues in sector C again", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_DURATION_BREAK;
        add(time, () -> scientistLeft.setSpeech("Sigh... Let's go", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_DURATION_BREAK;
        add(time, () -> {
            scientistRight.setWalkDirection(1);
            scientistLeft.setWalkDirection(1);
        });
        time += 0.4f;
        add(time, () -> scientistRight.applyVerticalImpulse(1.35f));
        time += 0.25f;
        add(time, () -> scientistLeft.applyVerticalImpulse(1.3f));
        time += 1.7f;
        add(time, scientistRight::remove);
        time += 0.3f;
        add(time, scientistLeft::remove);

        TimeMachine timeMachine = new TimeMachine();
        map.addObject(timeMachine);
        timeMachine.getBody().setTransform(getTileCenter(10, GROUND_TILE_Y + 0.25f), 0);

        Player player = map.getPlayer();
        player.getBody().setTransform(getTileCenter(6, GROUND_TILE_Y), 0);
        player.setHasTimeMachine(false);

        Decoration box = new Decoration(1, 8);
        map.addObject(box);
        box.getBody().setTransform(getTileCenter(7, GROUND_TILE_Y), 0);

        time = AD_DURATION;
        time += 2;
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
            add(time, () -> player.setWalkDirection(0));
            time += 1;
            add(time, () -> turbinePiece.setVisible(true));
            time += 1;
            add(time, () -> player.setWalkDirection(1));
            time += walkDuration;
            add(time, () -> player.setWalkDirection(0));
            time += 1;
            i++;
        }
        add(time, () -> {
            player.setWalkDirection(0);
            player.setSpeech("Finished", SPEECH_DURATION_LONG);
        });
        time += SPEECH_DURATION_LONG + SPEECH_DURATION_BREAK;
        time += 0.8f;
        add(time, () -> player.setSpeech("...", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_DURATION_BREAK;
        time += 0.3f;
        add(time, () -> player.setSpeech("!", SPEECH_DURATION_SHORT));
        time += SPEECH_DURATION_SHORT + SPEECH_DURATION_BREAK;
        add(time, () -> player.setWalkDirection(1));
        time += 0.7f;
        add(time, () -> player.setWalkDirection(0));
        time += 0.5f;
        add(time, () -> player.setSpeech("...", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_DURATION_BREAK;
        time += 0.8f;
        add(time, () -> player.setWalkDirection(1));
        time += 0.3f;

        duration = time;
    }
    private static final int GROUND_TILE_Y = 2;
    private static final int AD_DURATION = 3;
    private static final int ZOOM_OUT_DURATION = 4;
    private static final Rectangle FULL_ZOOM_IN_BOUNDS = new Rectangle(7, 1.375f, 0.03f, 0.03f);
    private static final float SPEECH_DURATION_LONG = 2.2f;
    private static final float SPEECH_DURATION_BREAK = 0.2f;
    private static final float SPEECH_DURATION_SHORT = 1;
    private List<Decoration> turbinePieces;

    @Override
    public void finish() {
        super.finish();
        map.getMusicState().play("main");
        for (Decoration turbine : turbinePieces) {
            turbine.setVisible(true);
        }
    }

    private Vector2 getTileCenter(float tileX, float tileY) {
        return new Vector2((tileX + 0.5f) * Map.TILE_SIZE, (tileY + 0.5f) * Map.TILE_SIZE);
    }

    @Override
    public void updateRenderBounds(Rectangle bounds) {
        super.updateRenderBounds(bounds);
        float time = map.getTime();
        if (time < (AD_DURATION + ZOOM_OUT_DURATION)) {
            float zoomOutProgress = 0;
            if (time >= AD_DURATION) {
                zoomOutProgress = ((time - AD_DURATION) / ZOOM_OUT_DURATION);
            }
            FloatUtil.lerp(FULL_ZOOM_IN_BOUNDS, bounds, zoomOutProgress, bounds);
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        super.render(spriteBatch, shapeRenderer);
        float time = map.getTime();
        if (time < AD_DURATION) {
            float adProgress = (time / AD_DURATION);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.BLACK.cpy().lerp(Color.WHITE, adProgress));
            shapeRenderer.rect(0, 0, Main.VIEWPORT_WIDTH, Main.VIEWPORT_HEIGHT);
            shapeRenderer.end();
        }
    }
}
