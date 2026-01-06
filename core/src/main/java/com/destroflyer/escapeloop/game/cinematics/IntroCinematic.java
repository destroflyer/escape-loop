package com.destroflyer.escapeloop.game.cinematics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.Cinematic;
import com.destroflyer.escapeloop.game.PlayMap;
import com.destroflyer.escapeloop.game.objects.Decoration;
import com.destroflyer.escapeloop.game.objects.Player;
import com.destroflyer.escapeloop.game.objects.Scientist;
import com.destroflyer.escapeloop.game.objects.TimeMachine;
import com.destroflyer.escapeloop.util.FloatUtil;
import com.destroflyer.escapeloop.util.RenderUtil;

import java.util.List;
import java.util.stream.Collectors;

public class IntroCinematic extends Cinematic {

    public IntroCinematic(PlayMap map) {
        super(map);

        Scientist scientistLeft = new Scientist();
        Scientist scientistRight = new Scientist();
        map.addObject(scientistLeft);
        map.addObject(scientistRight);
        scientistLeft.getBody().setTransform(getTileCenter(12, GROUND_TILE_Y), 0);
        scientistRight.getBody().setTransform(getTileCenter(15, GROUND_TILE_Y), 0);
        scientistRight.setViewDirection(-1);

        // Ad

        map.getAudioState().playMusic("intro", false);

        adSections = new IntroCinematicSection[] {
            new IntroCinematicSection("family", "Life is great."),
            new IntroCinematicSection("couch", "But all of us can use a little help."),
            new IntroCinematicSection("cat", "Life can get dangerous."),
            new IntroCinematicSection("math", "And sometimes, it needs a helping hand."),
            new IntroCinematicSection("laundry", "Some jobs are just more fun when shared."),
            new IntroCinematicSection("bed", "And every day should end with a smile."),
            new IntroCinematicSection("factory", "Orb Industries - The future is perfectly round", 4, 250),
        };
        adDuration = adSections.length * AD_SECTION_DURATION;

        // Scientists

        float time = adDuration + ZOOM_OUT_DURATION;
        time += 0.5f;
        add(time, () -> scientistLeft.setSpeech("Amazing", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_SHORT;
        add(time, () -> scientistRight.setSpeech("Yes, better than last year's ad", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_SHORT;
        add(time, () -> scientistLeft.setSpeech("I'm not a fan of the orbs", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_SHORT;
        add(time, () -> scientistLeft.setWalkDirection(-1));
        time += 0.25f;
        add(time, () -> scientistLeft.setWalkDirection(0));
        time += 0.5f;
        add(time, () -> scientistLeft.setSpeech("But they fund our actual project", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_SHORT;
        add(time, () -> scientistRight.setWalkDirection(-1));
        time += 0.7f;
        add(time, () -> scientistRight.setWalkDirection(0));
        time += 0.5f;
        add(time, () -> scientistRight.setSpeech("The time machine...", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_SHORT;
        add(time, () -> scientistRight.setSpeech("It still has a lot of quirks", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_SHORT;
        time += 1;
        add(time, () -> {
            map.getAudioState().playSound("alarm");
            scientistLeft.setViewDirection(1);
            scientistRight.setViewDirection(1);
            scientistRight.setSpeech("!", SPEECH_DURATION_SHORT);
        });
        time += SPEECH_DURATION_SHORT + SPEECH_BREAK_SHORT;
        add(time, () -> scientistRight.setSpeech("Issues in sector C again", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_SHORT;
        add(time, () -> scientistLeft.setSpeech("Sigh... Let's go", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_SHORT;
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

        // Player

        Player player = map.getPlayer();
        player.getBody().setTransform(getTileCenter(6, GROUND_TILE_Y), 0);
        player.setHasTimeMachine(false);

        Decoration box = new Decoration(1, 2);
        map.addObject(box);
        box.getBody().setTransform(getTileCenter(7, GROUND_TILE_Y), 0);

        TimeMachine timeMachine = new TimeMachine();
        map.addObject(timeMachine);
        timeMachine.getBody().setTransform(getTileCenter(10, GROUND_TILE_Y + 0.25f), 0);

        time = adDuration;
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
        add(time, () -> player.setWalkDirection(0));
        time += 0.5f;
        add(time, () -> player.setSpeech("Task queue: Complete", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_BREAK_LONG;
        add(time, () -> player.setSpeech("Model: Work Unit " + map.getSkins().getPlayerSkin().getTitle(), SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_BREAK_LONG;
        add(time, () -> player.setSpeech("Status: Functional", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_BREAK_LONG;
        add(time, () -> player.setSpeech("Next task:", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_BREAK_LONG;
        time += 0.5f;
        add(time, () -> player.setSpeech("None", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_LONG;
        time += 0.75f;
        add(time, () -> player.setSpeech("?", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_LONG;
        time += 1.5f;
        add(time, () -> player.setSpeech("Replacement arrives in: 48h", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_BREAK_LONG;
        add(time, () -> player.setSpeech("!", SPEECH_DURATION_SHORT));
        time += 0.5f;
        float wiggleDuration = 0.1f;
        for (i = 0; i < 3; i++) {
            add(time, () -> player.setWalkDirection(1));
            time += wiggleDuration / 2;
            add(time, () -> player.setWalkDirection(-1));
            time += wiggleDuration;
            add(time, () -> player.setWalkDirection(1));
            time += wiggleDuration / 2;
        }
        add(time, () -> player.setWalkDirection(0));
        time += 1;
        time += SPEECH_DURATION_SHORT + SPEECH_BREAK_LONG;
        add(time, () -> player.setSpeech("Reason:", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_BREAK_LONG;
        time += 0.5f;
        add(time, () -> player.setSpeech("Obsolete model", SPEECH_DURATION_LONG));
        time += SPEECH_DURATION_LONG + SPEECH_BREAK_LONG;
        time += 1.5f;
        add(time, () -> player.setSpeech("<sad beep>", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_LONG;
        time += 1.5f;
        add(time, () -> player.setSpeech("!", SPEECH_DURATION_SHORT));
        time += SPEECH_DURATION_SHORT + SPEECH_BREAK_LONG;
        add(time, () -> player.setWalkDirection(1));
        time += 0.7f;
        add(time, () -> player.setWalkDirection(0));
        time += 1;
        add(time, () -> player.setSpeech("...", SPEECH_DURATION_MEDIUM));
        time += SPEECH_DURATION_MEDIUM + SPEECH_BREAK_LONG;
        time += 0.8f;
        add(time, () -> player.setWalkDirection(1));
        time += 0.3f;

        duration = time;
    }
    private static final float AD_SECTION_DURATION = 5.5f;
    private static final float AD_SECTION_TRANSITION_DURATION = 2;
    private static final float AD_SECTION_DURATION_BEFORE_TRANSITION = AD_SECTION_DURATION - AD_SECTION_TRANSITION_DURATION;
    private static final Color AD_TEXT_BACKDROP_COLOR = new Color(1, 1, 1, 0.75f);
    private static final int ZOOM_OUT_DURATION = 5;
    private static final Rectangle FULL_ZOOM_IN_BOUNDS = new Rectangle(7, 1.375f, 0.03f, 0.03f);
    private static final int GROUND_TILE_Y = 2;
    private IntroCinematicSection[] adSections;
    private float adDuration;
    private BitmapFont adTextFont = new BitmapFont();
    private GlyphLayout textLayout = new GlyphLayout();
    private List<Decoration> turbinePieces;

    @Override
    public void onPauseMenuOpen() {
        super.onPauseMenuOpen();
        map.getAudioState().pauseMusic();
    }

    @Override
    public void onPauseMenuClose() {
        super.onPauseMenuClose();
        map.getAudioState().resumeMusic();
    }

    @Override
    public void finish() {
        super.finish();
        map.getAudioState().playMusic("main");
        for (Decoration turbine : turbinePieces) {
            turbine.setVisible(true);
        }
    }

    @Override
    public void updateRenderBounds(Rectangle bounds) {
        super.updateRenderBounds(bounds);
        float time = map.getTime();
        if (time < (adDuration + ZOOM_OUT_DURATION)) {
            float zoomOutProgress = 0;
            if (time >= adDuration) {
                zoomOutProgress = ((time - adDuration) / ZOOM_OUT_DURATION);
            }
            FloatUtil.lerp(FULL_ZOOM_IN_BOUNDS, bounds, zoomOutProgress, bounds);
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        super.render(spriteBatch, shapeRenderer);
        float time = map.getTime();
        if (time < adDuration) {
            int adSectionIndex = (int) ((time / adDuration) * adSections.length);
            IntroCinematicSection adSection = adSections[adSectionIndex];

            float adSectionTime = time % AD_SECTION_DURATION;
            Float transitionInProgress = null;
            Float transitionOutProgress = null;
            if (adSectionTime < AD_SECTION_TRANSITION_DURATION) {
                transitionInProgress = adSectionTime / AD_SECTION_TRANSITION_DURATION;
            } else if (adSectionTime > AD_SECTION_DURATION_BEFORE_TRANSITION) {
                transitionOutProgress = (adSectionTime - AD_SECTION_DURATION_BEFORE_TRANSITION) / AD_SECTION_TRANSITION_DURATION;
            }

            Color adTextureColor = Color.WHITE.cpy();
            Color adTextColor = Color.BLACK.cpy();
            if (transitionInProgress != null) {
                adTextureColor.a = transitionInProgress;
                adTextColor.a = transitionInProgress;
            } else if (transitionOutProgress != null) {
                adTextureColor.a = 1 - transitionOutProgress;
                adTextColor.a = 1 - transitionOutProgress;
            }

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(0, 0, Main.VIEWPORT_WIDTH, Main.VIEWPORT_HEIGHT);
            shapeRenderer.end();

            spriteBatch.begin();
            spriteBatch.setColor(adTextureColor);
            spriteBatch.draw(adSection.getTexture(), 0, 0, Main.VIEWPORT_WIDTH, Main.VIEWPORT_HEIGHT);
            spriteBatch.end();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            adTextFont.getData().setScale(adSection.getTextScale());
            RenderUtil.drawCenteredText(
                spriteBatch, textLayout, adTextFont, Main.VIEWPORT_WIDTH / 2, adSection.getTextY(), adSection.getText(), adTextColor, Main.VIEWPORT_WIDTH,
                shapeRenderer, AD_TEXT_BACKDROP_COLOR, 10f
            );
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }
}
