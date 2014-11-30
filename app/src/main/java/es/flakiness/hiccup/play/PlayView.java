package es.flakiness.hiccup.play;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;
import es.flakiness.hiccup.R;
import rx.Observable;

public class PlayView extends FrameLayout {

    @Inject Player player;
    @InjectView(R.id.play_view_debug_text) TextView debugText;
    @InjectView(R.id.play_view_gesture) PlayGestureView gesture;

    public PlayView(Context context) {
        this(context, null);
    }

    public PlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PlayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        // TODO(omo): This should be extracted to something like "inflateAndInject()"
        LayoutInflater.from(getContext()).inflate(R.layout.play_view, this);
        ButterKnife.inject(this);
    }

    public void injectFrom(ObjectGraph graph) {
        graph.inject(this);
        player.connectTo(gesture.gestures());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        player.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        player.onHostClose();
    }
}
