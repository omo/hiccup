package es.flakiness.hiccup;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;

public class TalkView extends CardView {
    public TalkView(Context context) {
        this(context, null);
    }

    public TalkView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TalkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.talk_view, this);
    }
}
