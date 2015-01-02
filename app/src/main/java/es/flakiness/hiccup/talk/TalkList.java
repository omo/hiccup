package es.flakiness.hiccup.talk;

import android.content.Intent;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import es.flakiness.hiccup.LeaveTalkEvent;
import es.flakiness.hiccup.PlayTalkEvent;

@Singleton
public class TalkList implements ListAdapter {

    TalkStore store;
    Bus bus;
    DataSetObservable observable = new DataSetObservable();
    List<TalkPreso> presoList = new ArrayList();

    @Inject public TalkList(TalkStore store, Bus bus) {
        this.store = store;
        this.bus = bus;
        // TODO(omo): Should we unregister somehow?
        this.bus.register(this);
        notifyChanged();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        observable.registerObserver(dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        observable.unregisterObserver(dataSetObserver);
    }

    @Override
    public int getCount() {
        return presoList.size();
    }

    @Override
    public Object getItem(int i) {
        return presoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return presoList.get(i).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TalkView talkView = null == view ? createFreshView(viewGroup) : (TalkView)view;
        talkView.setPreso(presoList.get(i));
        return talkView;
    }

    private TalkView createFreshView(ViewGroup viewGroup) {
        TalkView talkView = new TalkView(viewGroup.getContext());
        talkView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return talkView;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public static List<TalkPreso> toPresoList(List<Talk> entities) {
        List<TalkPreso> result = new ArrayList();
        for (Talk e : entities)
            result.add(new TalkPreso(e));
        return result;
    }

    @Subscribe public void addDebugTalk(AddDebugTalkEvent event) {
        store.putDebugInstance();
        notifyChanged();
    }

    @Subscribe public void clearTalk(ClearTalkEvent event) {
        store.clear();
        notifyChanged();
    }

    @Subscribe public void didLeaveTalk(LeaveTalkEvent event) {
        store.updateLastPosition(event.getUri(), event.getLeavingPosition());
        notifyChanged();
    }

    @Subscribe public void addTalk(AddTalkEvent event) {
        // TODO(omo): Should go background.
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(event.getContext(), event.getUri());

        if (Build.VERSION.SDK_INT >= 19) {
            event.getContext().getContentResolver().takePersistableUriPermission(event.getUri(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        Talk talk = new Talk(null, event.getUri().toString(), mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                             Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        store.put(talk);
        notifyChanged();
    }

    private void notifyChanged() {
        // TODO(omo): Should go background.
        presoList = toPresoList(store.list());
        observable.notifyChanged();
    }

    public void onClickAt(int i) {
        TalkPreso preso = presoList.get(i);
        bus.post(new PlayTalkEvent(preso.getUri(), preso.getLastPosition()));
    }
}
