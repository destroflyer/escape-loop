package com.destroflyer.escapeloop.game;

import lombok.Getter;
import lombok.Setter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.util.ArrayList;

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
    private Animation<TextureRegion> oneTimeAnimation;
    private float oneTimeAnimationStartTime;

    public abstract void createBody();

    public void update(float tpf) {
        for (Behaviour<?> behaviour : behaviours) {
            behaviour.update(tpf);
        }
        if ((oneTimeAnimation != null) && (map.getTime() >= (oneTimeAnimationStartTime + oneTimeAnimation.getAnimationDuration()))) {
            oneTimeAnimation = null;
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
        Vector2 impulse = directionToTarget.cpy().scl(bounceStrengthX, bounceStrengthY);
        body.setLinearVelocity(new Vector2());
        body.applyLinearImpulse(impulse, body.getPosition(), true);
    }

    // Rendering

    protected void setOneTimeAnimation(Animation<TextureRegion> animation) {
        oneTimeAnimation = animation;
        oneTimeAnimationStartTime = map.getTime();
    }

    public TextureRegion getCurrentTextureRegion() {
        if (oneTimeAnimation != null) {
            return oneTimeAnimation.getKeyFrame(map.getTime() - oneTimeAnimationStartTime, false);
        }
        Animation<TextureRegion> loopedAnimation = getLoopedAnimation();
        if (loopedAnimation != null) {
            return loopedAnimation.getKeyFrame(map.getTime(), true);
        }
        return null;
    }

    protected Animation<TextureRegion> getLoopedAnimation() {
        return null;
    }
}
