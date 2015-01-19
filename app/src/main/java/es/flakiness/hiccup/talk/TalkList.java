package es.flakiness.hiccup.talk;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import es.flakiness.hiccup.PlayTalkEvent;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class TalkList implements ListAdapter, Subscription {

    private final DataSetObservable observable = new DataSetObservable();
    private final Bus bus;
    private final TalkStore store;
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private List<TalkPreso> presoList = new ArrayList();

    @Inject public TalkList(TalkStore store, Bus bus) {
        this.store = store;
        this.bus = bus;
        subscriptions.add(this.store.changes().subscribe(new Action1<TalkStore>() {
            @Override
            public void call(TalkStore talkStore) {
                notifyChanged();
            }
        }));

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

    private void notifyChanged() {
        // TODO(omo): Should go background.
        presoList = toPresoList(store.list());
        observable.notifyChanged();
    }

    public void onClickAt(int i) {
        TalkPreso preso = presoList.get(i);
        bus.post(new PlayTalkEvent(preso.getUri(), preso.getLastPosition()));
    }

    @Override
    public void unsubscribe() {
        subscriptions.unsubscribe();
    }

    @Override
    public boolean isUnsubscribed() {
        return subscriptions.isUnsubscribed();
    }
}
