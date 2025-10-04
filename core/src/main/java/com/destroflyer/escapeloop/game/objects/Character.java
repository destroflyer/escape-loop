package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.destroflyer.escapeloop.game.Collisions;
import com.destroflyer.escapeloop.game.Direction;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.MapObject;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Character extends MapObject {

    // A bit smaller so characters can both walk through and jump in narrow paths
    protected static final float RADIUS = ((Map.TILE_SIZE / 2) - 0.015f);
    private static final float FOOT_SENSOR_HEIGHT = 0.05f;

    protected Fixture characterFixture;
    protected Fixture footSensorFixture;
    @Getter
    protected int walkDirection;
    @Getter
    @Setter
    protected int viewDirection = 1;
    @Getter
    @Setter
    protected int verticalDirection = 0;
    private float walkSpeed = 2.1f;
    private float airAcceleration = 0.05f;
    private float jumpImpulse = 0.9f;
    private float remainingGroundIgnoreTime;
    private ArrayList<MapObject> groundObjects = new ArrayList<>();
    @Getter
    private Item item;
    private float throwStrength = 3.8f;

    @Override
    public void createBody() {
        BodyDef characterBodyDef = new BodyDef();
        characterBodyDef.type = BodyDef.BodyType.DynamicBody;
        body = map.getWorld().createBody(characterBodyDef);
        body.setFixedRotation(true);
        body.setLinearDamping(0.2f);

        CircleShape characterShape = new CircleShape();
        characterShape.setRadius(RADIUS);
        FixtureDef characterFixtureDef = new FixtureDef();
        characterFixtureDef.shape = characterShape;
        characterFixtureDef.density = 1;
        characterFixture = body.createFixture(characterFixtureDef);

        Filter characterFilter = new Filter();
        characterFilter.categoryBits = Collisions.CHARACTER;
        characterFilter.maskBits = Collisions.GROUND | Collisions.CHARACTER | Collisions.FINISH | Collisions.ITEM | Collisions.TOGGLE_TRIGGER | Collisions.PRESSURE_TRIGGER | Collisions.BOUNCER | Collisions.BULLET;
        characterFixture.setFilterData(characterFilter);

        PolygonShape footSensorShape = new PolygonShape();
        footSensorShape.setAsBox(RADIUS / 2, FOOT_SENSOR_HEIGHT / 2, new Vector2(0, (-1 * RADIUS) - (FOOT_SENSOR_HEIGHT / 2)), 0);
        FixtureDef sensorFixtureDef = new FixtureDef();
        sensorFixtureDef.shape = footSensorShape;
        sensorFixtureDef.isSensor = true;
        footSensorFixture = body.createFixture(sensorFixtureDef);

        Filter sensorFilter = new Filter();
        sensorFilter.categoryBits = Collisions.CHARACTER_FOOT_SENSOR;
        sensorFilter.maskBits = Collisions.GROUND;
        footSensorFixture.setFilterData(sensorFilter);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        Vector2 linearVelocity = new Vector2(body.getLinearVelocity());
        MapObject ground = getGroundObject();
        if (ground != null) {
            Vector2 groundVelocity = ground.getBody().getLinearVelocity();
            if ((remainingGroundIgnoreTime <= 0) && (groundVelocity.len2() > 0)) {
                linearVelocity.set(ground.getBody().getLinearVelocity());
                linearVelocity.x += walkDirection * walkSpeed;
            } else {
                linearVelocity.x = walkDirection * walkSpeed;
            }
            body.setLinearVelocity(linearVelocity);
        } else if (walkDirection != 0) {
            boolean canStillAccelerate;
            if (walkDirection == 1) {
                canStillAccelerate = linearVelocity.x < walkSpeed;
            } else {
                canStillAccelerate = linearVelocity.x > (-1 * walkSpeed);
            }
            if (canStillAccelerate) {
                linearVelocity.x += (walkDirection * airAcceleration);
                body.setLinearVelocity(linearVelocity);
            }
        }

        remainingGroundIgnoreTime = Math.max(0, remainingGroundIgnoreTime - tpf);

        if (body.getPosition().y < -1) {
            remove(true);
        }
    }

    @Override
    public void onContactBegin(MapObject mapObject, Fixture ownFixture, Fixture otherFixture, Contact contact) {
        super.onContactBegin(mapObject, ownFixture, otherFixture, contact);
        if ((ownFixture == footSensorFixture) && isGroundObject(mapObject)) {
            groundObjects.add(mapObject);
        }
    }

    @Override
    public void onContactEnd(MapObject mapObject, Fixture ownFixture, Fixture otherFixture, Contact contact) {
        super.onContactEnd(mapObject, ownFixture, otherFixture, contact);
        if ((ownFixture == footSensorFixture) && isGroundObject(mapObject)) {
            groundObjects.remove(mapObject);
        }
    }

    protected boolean isGroundObject(MapObject mapObject) {
        return (mapObject instanceof Ground) || (mapObject instanceof Gate);
    }

    public void setWalkDirection(int walkDirection) {
        this.walkDirection = walkDirection;
        if (walkDirection != 0) {
            viewDirection = walkDirection;
        }
    }

    public void jump() {
        if (isOnGround()) {
            applyVerticalImpulse(jumpImpulse);
            map.getAudioState().playSound("jump");
        }
    }

    public void applyVerticalImpulse(float impulse) {
        body.applyLinearImpulse(new Vector2(0, impulse), body.getWorldCenter(), true);
        resetRemainingGroundIgnoreTime();
    }

    private void resetRemainingGroundIgnoreTime() {
        remainingGroundIgnoreTime = 0.1f;
    }

    public void pickup(Item item) {
        this.item = item;
        item.onPickup(this);
        map.getAudioState().playSound("pickup");
    }

    public void action() {
        if (item != null) {
            throwItem();
        } else {
            ToggleTrigger toggleTrigger = getTouchedToggleTrigger();
            if (toggleTrigger != null) {
                toggleTrigger.toggle();
            }
        }
        map.getAudioState().playSound("action");
    }

    private void throwItem() {
        float throwHorizontalDirection;
        float throwVerticalDirection;
        if (verticalDirection == 1) {
            throwHorizontalDirection = walkDirection * 0.5f;
            throwVerticalDirection = 1.5f;
        } else if (verticalDirection == -1) {
            throwHorizontalDirection = walkDirection * 0.9f;
            throwVerticalDirection = -0.5f;
        } else {
            throwHorizontalDirection = viewDirection;
            throwVerticalDirection = 1;
        }
        item.onThrow(new Vector2(throwHorizontalDirection * throwStrength, throwVerticalDirection * throwStrength));
        item = null;
    }

    private ToggleTrigger getTouchedToggleTrigger() {
        for (MapObject mapObject : activeContacts) {
            if (mapObject instanceof ToggleTrigger) {
                return (ToggleTrigger) mapObject;
            }
        }
        return null;
    }

    public MapObject getGroundObject() {
        return isOnGround() ? groundObjects.get(groundObjects.size() - 1) : null;
    }

    public boolean isOnGround() {
        return groundObjects.size() > 0;
    }

    @Override
    public void remove() {
        super.remove();
        if (item != null) {
            item.remove();
        }
    }

    @Override
    public Direction getTextureDirection() {
        return (viewDirection == -1) ? Direction.LEFT : Direction.RIGHT;
    }
}
