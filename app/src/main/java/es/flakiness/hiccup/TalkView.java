package es.flakiness.hiccup;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TalkView extends CardView {
    private TalkPreso preso;

    @InjectView(R.id.talk_title)
    TextView title;

    @InjectView(R.id.talk_duration)
    TextView duration;

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
        ButterKnife.inject(this);
    }

    public void setPreso(TalkPreso preso) {
        this.preso = preso;
        title.setText(preso.getTitle());
        duration.setText(preso.getDuration());
    }
}
