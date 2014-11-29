package es.flakiness.hiccup;

import javax.inject.Inject;

import nl.qbusict.cupboard.DatabaseCompartment;

public class TalkStore {
    private DatabaseCompartment database;

    @Inject TalkStore(DatabaseCompartment database) {
        this.database = database;
    }
}
