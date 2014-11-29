package es.flakiness.hiccup;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

public class TalkList implements ListAdapter {

    DataSetObservable observable = new DataSetObservable();
    List<TalkPreso> presoList = new ArrayList();

    public TalkList() {
        presoList.add(new TalkPreso());
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
}
