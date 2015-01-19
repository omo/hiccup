package es.flakiness.hiccup.index;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import es.flakiness.hiccup.Injections;
import es.flakiness.hiccup.R;

public class TalkView extends CardView {

    @InjectView(R.id.talk_title) TextView title;
    @InjectView(R.id.talk_duration) TextView duration;

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
        setContentPadding(10, 10, 10, 10);
   }

    public void setPreso(TalkPreso preso) {
        title.setText(preso.getTitle());
        duration.setText(preso.getDuration());
    }

    @Override
    public void setActivated(boolean selected) {
        super.setActivated(selected);
        int color = isActivated() ? Color.rgb(0xdd, 0xdd, 0xff) : Color.WHITE;
        setCardBackgroundColor(color);
    }
}