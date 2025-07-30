package com.destroflyer.escapeloop.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class MapContactListener implements ContactListener {

    public MapContactListener(Map map) {
        this.map = map;
    }
    private Map map;

    @Override
    public void beginContact(Contact contact) {
        MapObject mapObjectA = map.getMapObject(contact.getFixtureA());
        MapObject mapObjectB = map.getMapObject(contact.getFixtureB());
        mapObjectA.onContactBegin(mapObjectB, contact.getFixtureA(), contact.getFixtureB(), contact);
        mapObjectB.onContactBegin(mapObjectA, contact.getFixtureB(), contact.getFixtureA(), contact);
    }

    @Override
    public void endContact(Contact contact) {
        MapObject mapObjectA = map.getMapObject(contact.getFixtureA());
        MapObject mapObjectB = map.getMapObject(contact.getFixtureB());
        // endContact is called when bodies are getting destroyed on map reset/cleanup
        if ((mapObjectA != null) && (mapObjectB != null)) {
            mapObjectA.onContactEnd(mapObjectB, contact.getFixtureA(), contact.getFixtureB(), contact);
            mapObjectB.onContactEnd(mapObjectA, contact.getFixtureB(), contact.getFixtureA(), contact);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        MapObject mapObjectA = map.getMapObject(contact.getFixtureA());
        MapObject mapObjectB = map.getMapObject(contact.getFixtureB());
        mapObjectA.preSolve(mapObjectB, contact.getFixtureA(), contact.getFixtureB(), contact);
        mapObjectB.preSolve(mapObjectA, contact.getFixtureB(), contact.getFixtureA(), contact);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Do nothing
    }
}
