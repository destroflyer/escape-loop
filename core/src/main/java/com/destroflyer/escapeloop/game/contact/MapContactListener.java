package com.destroflyer.escapeloop.game.contact;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.game.MapObject;

import java.util.ArrayList;
import java.util.Comparator;

public class MapContactListener implements ContactListener {

    public MapContactListener(Map map) {
        this.map = map;
    }
    private Map map;
    private ArrayList<ContactEvent> events = new ArrayList<>();

    @Override
    public void beginContact(Contact contact) {
        ContactObjects objects = getSortedContactObjects(contact);
        events.add(new ContactEvent(true, objects.getMapObject1(), objects.getMapObject2(), objects.getFixture1(), objects.getFixture2()));
    }

    @Override
    public void endContact(Contact contact) {
        ContactObjects objects = getSortedContactObjects(contact);
        // Objects could be missing because endContact is called when bodies are getting destroyed on map reset/cleanup
        if (objects != null) {
            events.add(new ContactEvent(false, objects.getMapObject1(), objects.getMapObject2(), objects.getFixture1(), objects.getFixture2()));
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        ContactObjects objects = getSortedContactObjects(contact);
        // Needs to be directly executed in order to be able to disable contacts (so they don't get processed in the solver)
        // Note that this method is not allowed to make any direct (non-queued) physics changes which would otherwise lead to undeterministic behavior!
        objects.getMapObject1().preSolve(objects.getMapObject2(), objects.getFixture1(), objects.getFixture2(), contact);
        objects.getMapObject2().preSolve(objects.getMapObject1(), objects.getFixture2(), objects.getFixture1(), contact);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Do nothing
    }

    public void handleContacts() {
        events.sort(
            Comparator.comparingInt((ContactEvent event) -> event.getMapObject1().getId())
                .thenComparingInt(event -> event.getMapObject2().getId())
                .thenComparingInt(event -> event.getMapObject1().getBody().getFixtureList().indexOf(event.getFixture1(), true))
                .thenComparingInt(event -> event.getMapObject2().getBody().getFixtureList().indexOf(event.getFixture2(), true))
        );
        for (ContactEvent event : events) {
            if (event.isBeginOrEnd()) {
                event.getMapObject1().onContactBegin(event.getMapObject2(), event.getFixture1(), event.getFixture2());
                event.getMapObject2().onContactBegin(event.getMapObject1(), event.getFixture2(), event.getFixture1());
            } else {
                event.getMapObject1().onContactEnd(event.getMapObject2(), event.getFixture1(), event.getFixture2());
                event.getMapObject2().onContactEnd(event.getMapObject1(), event.getFixture2(), event.getFixture1());
            }
        }
        events.clear();
    }

    private ContactObjects getSortedContactObjects(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        MapObject mapObjectA = map.getMapObject(fixtureA);
        MapObject mapObjectB = map.getMapObject(fixtureB);
        if ((mapObjectA != null) && (mapObjectB != null)) {
            return (mapObjectA.getId() < mapObjectB.getId())
                ? new ContactObjects(mapObjectA, mapObjectB, fixtureA, fixtureB)
                : new ContactObjects(mapObjectB, mapObjectA, fixtureB, fixtureA);
        }
        return null;
    }
}
