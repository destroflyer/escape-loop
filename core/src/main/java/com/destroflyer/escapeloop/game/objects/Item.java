package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.destroflyer.escapeloop.game.Collisions;
import com.destroflyer.escapeloop.game.MapObject;

import java.util.ArrayList;

public class Item extends MapObject {

    public Item(String identifier) {
        textureSize = new Vector2(RADIUS * 2, RADIUS * 2);
        textureRegion = new TextureRegion(new Texture("./textures/items/" + identifier + ".png"));
    }
    private static final float RADIUS = 0.15f;
    private TextureRegion textureRegion;
    protected Character holder;
    protected Character thrower;
    private ArrayList<Character> blockedPickupCharacters = new ArrayList<>();

    @Override
    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = map.getWorld().createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(RADIUS);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 2.5f;
        Fixture fixture = body.createFixture(fixtureDef);

        Filter filter = new Filter();
        filter.categoryBits = Collisions.ITEM;
        filter.maskBits = Collisions.PLATFORM | Collisions.CHARACTER | Collisions.BOUNCER;
        fixture.setFilterData(filter);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (holder != null) {
            body.setTransform(holder.getBody().getPosition(), holder.getBody().getAngle());
        }
    }

    @Override
    public void onContactBegin(MapObject mapObject, Fixture ownFixture, Fixture otherFixture, Contact contact) {
        super.onContactBegin(mapObject, ownFixture, otherFixture, contact);
        if ((holder == null) && (mapObject instanceof Character)) {
            Character character = (Character) mapObject;
            if (!blockedPickupCharacters.contains(character)) {
                boolean canBePickuped = true;
                if (thrower != null) {
                    canBePickuped = onImpact(thrower, character);
                    thrower = null;
                }
                if (canBePickuped && (character.getItem() == null)) {
                    character.pickup(this);
                }
            }
        }
    }

    @Override
    public void onContactEnd(MapObject mapObject, Fixture ownFixture, Fixture otherFixture, Contact contact) {
        super.onContactEnd(mapObject, ownFixture, otherFixture, contact);
        blockedPickupCharacters.remove(mapObject);
    }

    @Override
    public void preSolve(MapObject mapObject, Fixture ownFixture, Fixture otherFixture, Contact contact) {
        super.preSolve(mapObject, ownFixture, otherFixture, contact);
        if (mapObject instanceof Character) {
            contact.setEnabled(false);
        }
    }

    protected boolean onImpact(Character thrower, Character target) {
        return true;
    }

    protected void onPickup(Character holder) {
        this.holder = holder;
        map.queueTask(() -> body.setActive(false));
    }

    protected void onThrow(Vector2 velocity) {
        thrower = holder;
        holder = null;
        blockedPickupCharacters.add(thrower);
        body.setLinearVelocity(velocity);
        body.setActive(true);
    }

    @Override
    public TextureRegion getTextureRegion() {
        return textureRegion;
    }
}
