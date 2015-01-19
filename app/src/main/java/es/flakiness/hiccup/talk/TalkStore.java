package es.flakiness.hiccup.talk;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.qbusict.cupboard.DatabaseCompartment;
import rx.Observable;
import rx.subjects.PublishSubject;

@Singleton
public class TalkStore {
    private PublishSubject<TalkStore> changes = PublishSubject.create();
    private DatabaseCompartment database;

    @Inject
    public TalkStore(DatabaseCompartment database) {
        this.database = database;
    }

    private void notifyChanged() {
        changes.onNext(this);
    }

    public void putDebugInstance() {
        Talk t = new Talk(null, "uri://debug/", "Hello Debug Talk!", new Long(0));
        database.put(t);
        notifyChanged();
    }

    public Observable<TalkStore> changes() { return changes; }

    private void put(Talk talk) {
        // TODO(omo): Should go background.
        database.put(talk);
        notifyChanged();
    }

    private void clear() {
        // TODO(omo): Should go background.
        database.delete(Talk.class, null);
        notifyChanged();
    }

    private void remove(List<Long> ids) {
        // TODO(omo): Should go background.
        for (Long id : ids) {
            database.delete(Talk.class, id.longValue());
        }

        notifyChanged();
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
        notifyChanged();
    }

    public void addDebugTalk(AddDebugTalkEvent event) {
        putDebugInstance();
    }

    public void clearTalk(ClearTalkEvent event) {
        clear();
    }

    public void addTalk(AddTalkEvent event) {
        // TODO(omo): Should go background.
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(event.getContext(), event.getUri());

        if (Build.VERSION.SDK_INT >= 19) {
            event.getContext().getContentResolver().takePersistableUriPermission(event.getUri(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        Talk talk = new Talk(null, event.getUri().toString(), mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        put(talk);
    }

    public void removeTalks(RemoveTalksEvent event) {
        remove(event.getRemovedItems());
    }

}
