package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.destroflyer.escapeloop.game.Collisions;
import com.destroflyer.escapeloop.game.Direction;
import com.destroflyer.escapeloop.game.MapObject;
import com.destroflyer.escapeloop.util.TextureUtil;

public class Bullet extends MapObject {

    public Bullet(Enemy shooter) {
        this.shooter = shooter;
    }
    private static final Animation<TextureRegion> ANIMATION_FLYING = TextureUtil.loadWrappedAnimation("./textures/enemy_robot/bullet.png", 2, 2, 3, 0.2f);
    private Enemy shooter;

    @Override
    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.gravityScale = 0;
        body = map.getWorld().createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.1f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.isSensor = true;
        Fixture fixture = body.createFixture(fixtureDef);

        Filter filter = new Filter();
        filter.categoryBits = Collisions.BULLET;
        filter.maskBits = Collisions.GROUND | Collisions.CHARACTER | Collisions.TOGGLE_TRIGGER | Collisions.BULLET;
        fixture.setFilterData(filter);
    }

    @Override
    public void onContactBegin(MapObject mapObject, Fixture ownFixture, Fixture otherFixture, Contact contact) {
        super.onContactBegin(mapObject, ownFixture, otherFixture, contact);
        if (mapObject != shooter) {
            if (mapObject instanceof Character) {
                mapObject.remove();
            } else if (mapObject instanceof ToggleTrigger) {
                ToggleTrigger toggleTrigger = (ToggleTrigger) mapObject;
                toggleTrigger.toggle();
            }
            remove();
        }
    }

    @Override
    public Direction getTextureDirection() {
        return (Math.signum(body.getLinearVelocity().x) == -1 ? Direction.LEFT : Direction.RIGHT);
    }

    @Override
    protected Animation<TextureRegion> getLoopedAnimation() {
        return ANIMATION_FLYING;
    }
}
