package com.destroflyer.escapeloop.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.destroflyer.escapeloop.game.Collisions;
import com.destroflyer.escapeloop.game.Direction;
import com.destroflyer.escapeloop.game.MapObject;

public class Bullet extends MapObject {

    public Bullet(Enemy shooter) {
        this.shooter = shooter;
    }
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
        circleShape.dispose();

        Filter filter = new Filter();
        filter.categoryBits = Collisions.BULLET;
        filter.maskBits = Collisions.GROUND | Collisions.CHARACTER | Collisions.TOGGLE_TRIGGER | Collisions.BULLET;
        fixture.setFilterData(filter);
    }

    @Override
    public void onContactBegin(MapObject mapObject, Fixture ownFixture, Fixture otherFixture) {
        super.onContactBegin(mapObject, ownFixture, otherFixture);
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
        return shooter.getAnimations().getBulletFlyingAnimation();
    }
}
