package es.flakiness.hiccup.play;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
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
import rx.subscriptions.CompositeSubscription;

public class PlayView extends FrameLayout {

    @Inject GestureInterpreter interpreter;
    @Inject Playing playing;
    @Inject PlayClockPreso clockPreso;
    @InjectView(R.id.play_view_debug_text) TextView debugText;
    @InjectView(R.id.play_view_clock) TextView clockText;
    @InjectView(R.id.play_view_gesture) PlayGestureView gesture;
    @InjectView(R.id.play_view_bar) PlayBarView barView;

    private CompositeSubscription subscriptions;

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

    // FIXME: It is confusing to have two initialization code: injectFrom() and onAttachedToWindow()
    public void injectFrom(ObjectGraph graph) {
        graph.inject(this);
        subscriptions = new CompositeSubscription();

        interpreter.connectTo(gesture.gestures());
        clockPreso.connectTo(clockText, barView);

        subscriptions.add(playing.states().subscribe(new Action1<PlayerState>() {
            @Override
            public void call(PlayerState playerState) {
                debugText.setText(playerState.toString());
            }
        }));

        // https://www.google.com/fonts/specimen/Source+Sans+Pro
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/SourceSansPro-Light.ttf");
        clockText.setTypeface(tf);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        interpreter.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        subscriptions.unsubscribe();
    }
}
