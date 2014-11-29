package es.flakiness.hiccup;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IndexView extends FrameLayout {
    @InjectView(R.id.card_list)
    ListView cardList;

    // TODO(morrita): Inject.
    TalkStore store = new TalkStore();

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
        LayoutInflater.from(getContext()).inflate(R.layout.index_view, this);
        ButterKnife.inject(this);
        cardList.setAdapter(new TalkList());
    }
}
