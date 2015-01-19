package es.flakiness.hiccup.index;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import javax.inject.Inject;

import butterknife.InjectView;
import es.flakiness.hiccup.Injections;
import es.flakiness.hiccup.R;
import es.flakiness.hiccup.talk.TalkStore;

public class IndexView extends FrameLayout {
    @Inject TalkList talkList;
    @Inject
    TalkStore store;
    @InjectView(R.id.card_list) ListView cardList;

    private TalkListActionMode actionMode;

    public IndexView(Context context) {
        this(context, null);
    }

    public IndexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IndexView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        Injections.inflateAndInject(R.layout.index_view, this);
        cardList.setAdapter(talkList);
        cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (actionMode.isActive()) {
                    actionMode.viewWasSelectedWhileActive(view, i);
                    return;
                }

                talkList.onClickAt(i);
            }
        });

        actionMode = new TalkListActionMode(cardList, store); // FIXME(omo): This should be built by Dagger.
        cardList.setOnItemLongClickListener(actionMode);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        talkList.unsubscribe(); // This should be handled by the module
    }
}