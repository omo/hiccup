package es.flakiness.hiccup.play;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

enum PlayerState {
    PREPARING,
    PREPARED,
    PLAYING,
    PAUSING,
    HOLDING,
};

public class Player {
    private final MediaPlayer player;
    private final Uri uri;
    private boolean startRequested;
    private PlayerState state;
    private Subscription gestureSubscription;

    public Player(Context context, Uri uri) throws IOException {
        this.uri = uri;
        this.player = new MediaPlayer();
        this.player.setDataSource(context, uri);
        this.player.prepareAsync();
        this.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Player.this.onPlayerPrepared();
            }
        });

        this.state = PlayerState.PREPARING;
    }

    public void onPlayerPrepared() {
        state = PlayerState.PREPARED;
        startIfNeededAndPossible();
    }

    public void start() {
        startRequested = true;
        startIfNeededAndPossible();
    }

    private void startIfNeededAndPossible() {
       if (startRequested && (state != PlayerState.PREPARED || state != PlayerState.PAUSING)) {
           player.start();
           state = PlayerState.PLAYING;
       }
    }

    public void toggle() {
        if (state == PlayerState.PLAYING)
            pause();
        else
            start();
    }

    private void pause() {
        // TODO(morrita): Handle unprepared case.
        player.pause();
        state = PlayerState.PAUSING;
    }

    private void hold() {
        // TODO(morrita): Handle unprepared case.
        if (state != PlayerState.PAUSING || state != PlayerState.HOLDING)
            player.pause();
        state = PlayerState.HOLDING;
    }

    private void unholdIfHeld() {
        if (state != PlayerState.HOLDING)
            return;
        // TODO(morrita): Should stay paused if the last state is paused.
        start();
    }

    public void onHostClose() {
        player.stop();
        player.release();
        gestureSubscription.unsubscribe();
    }

    public void connectTo(Observable<GestureEvent> gestures) {
        gestureSubscription = gestures.subscribe(new Action1<GestureEvent>() {
            @Override
            public void call(GestureEvent gestureEvent) {
                switch (gestureEvent.getType()) {
                    case TAP:
                        toggle();
                        break;
                    case HOLD:
                        hold();
                        break;
                    case MAY_UNHOLD:
                        unholdIfHeld();
                        break;
                }
            }
        });
    }
}
