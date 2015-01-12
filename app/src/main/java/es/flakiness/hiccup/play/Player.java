package es.flakiness.hiccup.play;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
* Created by morrita on 1/2/15.
*/
public class Player implements PlayerProgressSource.Values, Playing {
    private interface PendingAction {
        boolean run();
    }

    private final Uri uri;
    private final Context context;
    private final MediaPlayer media;
    private List<PendingAction> pendingActions = new ArrayList(); // FIXME: Could be different class.
    private PlayerProgressSource progressSource;
    private PublishSubject<PlayerState> stateSubject = PublishSubject.create();
    private PlayerState state;

    public Player(Context context, Uri uri) throws IOException {
        this.progressSource = new PlayerProgressSource(this, new Handler(Looper.myLooper()));
        this.context = context;
        this.uri = uri;
        this.media = new MediaPlayer();

        this.media.setDataSource(context, uri);
        this.media.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                onPlayerPrepared();
            }
        });

        this.media.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                onPlayerCompleted();
            }
        });

        this.media.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                onPlayerSeekComplete();
            }
        });

        prepareStarting();
    }

    public Context getContext() {
        return context;
    }

    public PlayerState getState() {
        return state;
    }

    private void consumePendingActionsWhilePossible() {
        while (!pendingActions.isEmpty()) {
            boolean done = pendingActions.get(0).run();
            if (!done)
                break;
            pendingActions.remove(0);
        }
    }

    private void onPlayerSeekComplete() {
        if (state != PlayerState.SEEKING) {
            // This does happen. MediaPlayer calls onPlayerSeekComplete() even when
            // the user doesn't request seekTo().
            return;
        }

        if (media.isPlaying())
            throw new AssertionError("The GestureInterpreter should be paused after seeking.");
        setState(PlayerState.PAUSING);
        consumePendingActionsWhilePossible();
        progressSource.emit();
    }

    private void onPlayerCompleted() {
        seekTo(media.getDuration() - 1);
    }

    private void onPlayerPrepared() {
        progressSource.start();

        setState(PlayerState.PREPARED);
        consumePendingActionsWhilePossible();
    }

    private void prepareStarting() {
        this.media.prepareAsync();
        setState(PlayerState.PREPARING);
    }

    public void start() {
        pendingActions.add(new PendingAction() {
            @Override
            public boolean run() {
                if (!state.isReadyToStart())
                    return false;
                media.start();
                setState(PlayerState.PLAYING);
                return true;
            }
        });

        consumePendingActionsWhilePossible();
    }

    public void pause() {
        // FIXME: This should be pend-able.
        if (state.isPauseable()) {
            media.pause();
            setState(PlayerState.PAUSING);
        }
    }

    public void toggle() {
        if (state == PlayerState.PLAYING)
            pause();
        else
            start();
    }

    // FIXME: This is wrong responsibility.
    public void hold() {
        media.pause();
        setState(PlayerState.HOLDING);
    }

    public void seekTo(final int nextPosition) {
        pendingActions.add(new PendingAction() {
            @Override
            public boolean run() {
                if (state.isBusy())
                    return false;
                setState(PlayerState.SEEKING);
                media.pause(); // This guarantees that the interpreter goes back to pause after seeking.
                media.seekTo(nextPosition);
                return true;
            }
        });

        consumePendingActionsWhilePossible();
    }

    private void setState(PlayerState state) {
        if (this.state == state)
            return;
        this.state = state;
        stateSubject.onNext(state);
    }

    public void emit(PlayerProgress progress) {
        progressSource.emit(progress);
    }

    @Override
    public Uri getUri() {
        return uri;
    }

    @Override
    public Observable<PlayerProgress> progress() {
        return progressSource.getObservable();
    }

    @Override
    public Observable<PlayerState> states() {
        return stateSubject;
    }

    public void release() {
        media.stop();
        media.release();
        progressSource.stop();
        stateSubject.onCompleted();
    }

    @Override
    public PlayerProgress getProgress() {
        if (!state.shouldEmit())
            return null;
        return new PlayerProgress(media.getDuration(), media.getCurrentPosition());
    }

    @Override
    public int getCurrentPosition() {
        return media.getCurrentPosition();
    }
}
