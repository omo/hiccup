package es.flakiness.hiccup.talk;

import android.net.Uri;

import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.qbusict.cupboard.DatabaseCompartment;

@Singleton
public class TalkStore {
    private DatabaseCompartment database;

    @Inject TalkStore(DatabaseCompartment database, Bus bus) {
        this.database = database;
    }

    public void putDebugInstance() {
        Talk t = new Talk(null, "uri://debug/", "Hello Debug Talk!", new Long(0));
        database.put(t);
    }

    public void put(Talk talk) {
        // TODO(omo): Should go background.
        database.put(talk);
    }

    public void clear() {
        // TODO(omo): Should go background.
        database.delete(Talk.class, null);
    }

    public List<Talk> list() {
        // TODO(omo): Should go background.
        // TODO(omo): Verify the URL and remove the entity if it has gone invalid.
        return database.query(Talk.class).list();
    }

    public void updateLastPosition(Uri uri, int lastPosition) {
        // TODO(omo): Should go background.
        Talk found = database.query(Talk.class).withSelection("uri = ?", uri.toString()).get();
        found.lastPosition = new Long(lastPosition);
        database.put(found);
    }
}
