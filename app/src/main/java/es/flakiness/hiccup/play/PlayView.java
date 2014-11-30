package es.flakiness.hiccup.play;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.io.IOException;

import javax.inject.Inject;

public class PlayView extends FrameLayout {

    @Inject
    Player player;

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

    }

    public void setUri(Uri uri) throws IOException {
        this.player = new Player(getContext().getApplicationContext(), uri);
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
