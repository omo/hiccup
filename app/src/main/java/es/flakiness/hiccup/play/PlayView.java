package es.flakiness.hiccup.play;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;
import es.flakiness.hiccup.R;
import rx.functions.Action1;

public class PlayView extends FrameLayout {

    @Inject Player player;
    @InjectView(R.id.play_view_debug_text) TextView debugText;
    @InjectView(R.id.play_view_gesture) PlayGestureView gesture;

    private String debugStateText = "";
    private String debugProgressText = "";

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
        // TODO(omo): Unsubscribe.
        player.states().subscribe(new Action1<PlayerState>() {
            @Override
            public void call(PlayerState playerState) {
                onPlayerStateChanged(playerState);
            }
        });

        // TODO(omo): Unsubscribe.
        player.intervalProgress(1000).subscribe(new Action1<PlayerProgress>() {
            @Override
            public void call(PlayerProgress playerProgress) {
                onPlayerProgressUpdated(playerProgress);
            }
        });
    }

    private void onPlayerStateChanged(PlayerState playerState) {
        debugStateText = playerState.toString();
        debugText.setText(debugStateText + ":" + debugProgressText);
    }

    private String format(PlayerProgress progress) {
        int second = (progress.getCurrent()/1000)%60;
        int minute = (progress.getCurrent()/(60*1000))%60;
        return String.format("%02d:%02d", minute, second);
    }

    private void onPlayerProgressUpdated(PlayerProgress playerProgress) {
        debugProgressText = format(playerProgress);
        debugText.setText(debugStateText + ":" + debugProgressText);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        player.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        player.release();
    }
}
