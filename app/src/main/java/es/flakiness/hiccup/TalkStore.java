package es.flakiness.hiccup;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.qbusict.cupboard.DatabaseCompartment;

@Singleton
public class TalkStore {
    private DatabaseCompartment database;

    @Inject TalkStore(DatabaseCompartment database) {
        this.database = database;
    }

    public void putDebugInstance() {
        Talk t = new Talk(null, "uri://debug/", "Hello Debug Talk!", new Date(0));
        database.put(t);
    }

    public void clear() {
        database.delete(Talk.class, null);
    }

    public List<Talk> list() {
        return database.query(Talk.class).list();
    }
}
