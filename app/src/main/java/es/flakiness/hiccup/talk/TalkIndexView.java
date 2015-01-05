package es.flakiness.hiccup.talk;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import es.flakiness.hiccup.App;
import es.flakiness.hiccup.R;

public class TalkIndexView extends FrameLayout {
    @Inject TalkList talkList;
    @Inject Bus bus;
    @InjectView(R.id.card_list) ListView cardList;

    private TalkListActionMode actionMode;

    public TalkIndexView(Context context) {
        this(context, null);
    }

    public TalkIndexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TalkIndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TalkIndexView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.index_view, this);
        ButterKnife.inject(this);
        App.inject(getContext().getApplicationContext(), this);
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

        actionMode = new TalkListActionMode(cardList, bus); // FIXME(omo): This should be built by Dagger.
        cardList.setOnItemLongClickListener(actionMode);
    }
}
