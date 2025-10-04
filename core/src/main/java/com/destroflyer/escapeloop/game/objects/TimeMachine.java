package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.destroflyer.escapeloop.game.Collisions;
import com.destroflyer.escapeloop.game.MapObject;

public class TimeMachine extends MapObject {

    public TimeMachine() {
        textureSize = new Vector2(WIDTH, HEIGHT);
        textureRegion = new TextureRegion(new Texture("./textures/lab/time_machine.png"));
    }
    private static final float WIDTH = 0.1f;
    private static final float HEIGHT = (WIDTH * (11f / 4));
    private TextureRegion textureRegion;
    private Vector2 sourcePosition;

    @Override
    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        body = map.getWorld().createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(WIDTH / 2, HEIGHT / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;
        Fixture fixture = body.createFixture(fixtureDef);
        polygonShape.dispose();

        Filter filter = new Filter();
        filter.categoryBits = Collisions.TIME_MACHINE;
        filter.maskBits = Collisions.PLAYER;
        fixture.setFilterData(filter);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (sourcePosition == null) {
            sourcePosition = body.getPosition().cpy();
        }
        body.setTransform(sourcePosition.cpy().add(0, (float) (Math.sin(map.getTime() * 3)) * 0.05f), body.getAngle());
    }

    @Override
    public void onContactBegin(MapObject mapObject, Fixture ownFixture, Fixture otherFixture, Contact contact) {
        super.onContactBegin(mapObject, ownFixture, otherFixture, contact);
        if (mapObject instanceof Player) {
            Player player = (Player) mapObject;
            player.setHasTimeMachine(true);
            remove();
        }
    }

    @Override
    public TextureRegion getSimpleTextureRegion() {
        return textureRegion;
    }
}
