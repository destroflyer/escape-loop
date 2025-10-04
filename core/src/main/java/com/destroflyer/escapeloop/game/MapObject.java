package com.destroflyer.escapeloop.game;

import lombok.Getter;
import lombok.Setter;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public abstract class MapObject {

    @Setter
    protected Map map;
    @Getter
    protected Body body;
    private ArrayList<Behaviour<?>> behaviours = new ArrayList<>();
    protected ArrayList<MapObject> activeContacts = new ArrayList<>();
    @Getter
    protected Vector2 textureOffset = new Vector2();
    @Getter
    protected Vector2 textureSize = new Vector2(Map.TILE_SIZE, Map.TILE_SIZE);
    protected Animation<TextureRegion> removalAnimation;
    protected String removalSound;
    private Animation<TextureRegion> oneTimeAnimation;
    private float oneTimeAnimationStartTime;
    @Getter
    private String speech;
    private Float remainingSpeechDuration;
    @Getter
    @Setter
    private boolean visible = true;

    public abstract void createBody();

    public void update(float tpf) {
        for (Behaviour<?> behaviour : behaviours) {
            behaviour.update(tpf);
        }
        if ((oneTimeAnimation != null) && (map.getTime() >= (oneTimeAnimationStartTime + oneTimeAnimation.getAnimationDuration()))) {
            oneTimeAnimation = null;
        }
        if (remainingSpeechDuration != null) {
            remainingSpeechDuration -= tpf;
            if (remainingSpeechDuration <= 0) {
                speech = null;
                remainingSpeechDuration = null;
            }
        }
    }

    public void addBehaviour(Behaviour behaviour) {
        behaviour.setMapObject(this);
        behaviours.add(behaviour);
    }

    public void removeBehaviour(Behaviour behaviour) {
        behaviours.remove(behaviour);
    }

    public void onContactBegin(MapObject mapObject, Fixture ownFixture, Fixture otherFixture, Contact contact) {
        activeContacts.add(mapObject);
    }

    public void onContactEnd(MapObject mapObject, Fixture ownFixture, Fixture otherFixture, Contact contact) {
        activeContacts.remove(mapObject);
    }

    public void preSolve(MapObject mapObject, Fixture ownFixture, Fixture otherFixture, Contact contact) {

    }

    public void bounceOff(MapObject otherMapObject) {
        float bounceStrengthX = 0.5f + Math.min(Math.max(Math.abs(body.getLinearVelocity().x), Math.abs(otherMapObject.getBody().getLinearVelocity().x)) / 3, 1);
        float bounceStrengthY = 1;
        Vector2 directionToTarget = body.getPosition().cpy().sub(otherMapObject.getBody().getPosition()).nor();
        // Can for example happen during the first frame with two players at the starting position
        if (directionToTarget.isZero()) {
            // Guarantees the two objects to have different bounce directions
            directionToTarget.x = (hashCode() < otherMapObject.hashCode()) ? -1 : 1;
        }
        Vector2 impulse = directionToTarget.cpy().scl(bounceStrengthX, bounceStrengthY);
        body.setLinearVelocity(new Vector2());
        body.applyLinearImpulse(impulse, body.getPosition(), true);
        map.getAudioState().playSound("bounce");
    }

    public RayCastResult rayCast(Vector2 point1, Vector2 point2, Predicate<RayCastResult> filter) {
        AtomicReference<RayCastResult> validResult = new AtomicReference<>();
        map.getWorld().rayCast((fixture, point, normal, fraction) -> {
            MapObject mapObject = map.getMapObject(fixture);
            // Copy point as it seems to be internally reused and modified later
            RayCastResult result = new RayCastResult(mapObject, fixture, point.cpy());
            if (filter.test(result)) {
                validResult.set(result);
                return fraction;
            }
            return -1;
        }, point1, point2);
        return validResult.get();
    }

    public void remove() {
        remove(false);
    }

    public void remove(boolean instant) {
        float timeUntilRemoval = 0;
        if (!instant) {
            if (removalAnimation != null) {
                map.queueTask(() -> body.setActive(false));
                setOneTimeAnimation(removalAnimation);
                timeUntilRemoval = removalAnimation.getAnimationDuration();
            }
            if (removalSound != null) {
                map.getAudioState().playSound(removalSound);
            }
        }
        map.queueTask(() -> map.removeObject(this), timeUntilRemoval);
    }

    // Rendering

    public Direction getTextureDirection() {
        return Direction.RIGHT;
    }

    protected void setOneTimeAnimation(Animation<TextureRegion> animation) {
        oneTimeAnimation = animation;
        oneTimeAnimationStartTime = map.getTime();
    }

    public boolean hasTexture() {
        return getTextureRegion(0, 0,  1, 1) != null;
    }

    public TextureRegion getTextureRegion(int tileX, int tileY, int tilesX, int tilesY) {
        if (oneTimeAnimation != null) {
            return oneTimeAnimation.getKeyFrame(map.getTime() - oneTimeAnimationStartTime, false);
        }
        Animation<TextureRegion> loopedAnimation = getLoopedAnimation();
        if (loopedAnimation != null) {
            return loopedAnimation.getKeyFrame(map.getTime(), true);
        }
        return getSimpleTextureRegion();
    }

    public TextureRegion getSimpleTextureRegion() {
        return null;
    }

    protected Animation<TextureRegion> getLoopedAnimation() {
        return null;
    }

    public Particles getParticles() {
        return null;
    }

    public void setSpeech(String speech) {
        setSpeech(speech, null);
    }

    public void setSpeech(String speech, Float duration) {
        this.speech = speech;
        remainingSpeechDuration = duration;
    }
}
