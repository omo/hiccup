package es.flakiness.hiccup.play;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class Player {
    private final Context context;
    private final MediaPlayer player;
    private final Uri uri;
    private boolean startRequested;
    private PlayerState state;
    private Subscription gestureSubscription;
    private PublishSubject<PlayerState> stateSubject = PublishSubject.create();
    private PublishSubject<PlayerProgress> progressSubject = PublishSubject.create();
    private Seeker seeker;

    private static int UPDATE_INTERVAL = 1000;
    private Handler progressHandler;
    private Runnable postProgress = new Runnable() {
        @Override
        public void run() {
            notifyProgress();
            int delay = UPDATE_INTERVAL - player.getCurrentPosition() % UPDATE_INTERVAL;
            if (progressHandler != null)
                progressHandler.postDelayed(postProgress, delay);
        }
    };


    public Player(Context context, Uri uri) throws IOException {
        this.context = context;
        this.uri = uri;
        this.player = new MediaPlayer();

        // This makes sure that |player| stay STARTED state internally.
        this.player.setLooping(true);

        this.player.setDataSource(context, uri);
        this.player.prepareAsync();
        this.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Player.this.onPlayerPrepared();
            }
        });

        this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                onPlayerCompleted();
            }
        });

        this.player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                onPlayerSeekComplete();
            }
        });
        setState(PlayerState.PREPARING);

        this.progressHandler = new Handler(Looper.myLooper());
        this.progressHandler.postDelayed(postProgress, 0);
    }

    private void onPlayerSeekComplete() {
        Log.d("Player", "onPlayerSeekComplete");
        setState(PlayerState.SEEKED);
        startIfNeededAndPossible();
        notifyProgress();
    }

    private void onPlayerCompleted() {
        this.pause();
        setState(PlayerState.COMPLETED);
    }

    public void onPlayerPrepared() {
        setState(PlayerState.PREPARED);
        startIfNeededAndPossible();
    }

    public void start() {
        startRequested = true;
        startIfNeededAndPossible();
    }

    private void startIfNeededAndPossible() {
       if (startRequested && state.isReadyToStart()) {
           player.start();
           startRequested = false;
           setState(PlayerState.PLAYING);
       }
    }

    public void toggle() {
        if (state == PlayerState.PLAYING)
            pause();
        else
            start();
    }

    private void pause() {
        if (state.isPauseable()) {
            player.pause();
            setState(PlayerState.PAUSING);
        }
    }

    private void hold() {
        if (state.isPauseable()) {
            player.pause();
            setState(PlayerState.HOLDING);
            seeker = new Seeker(getProgress());
            seeker.currentPositions().subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    notifyProgress();
                }
            });
        }
    }

    private void unholdIfHolding() {
        if (state == PlayerState.HOLDING) {
            int nextPosition = seeker.release();
            Log.d("Player", String.format("delta:%d", Math.abs(nextPosition - player.getCurrentPosition())));
            if (1000 < Math.abs(nextPosition - player.getCurrentPosition())) {
                setState(PlayerState.SEEKING);
                this.player.seekTo(nextPosition);
            }

            seeker = null;
            notifyProgress();
            start();
        }
    }

    private void pull(float gradient) {
        seeker.setGradient(gradient);
    }

    public void release() {
        player.stop();
        player.release();
        gestureSubscription.unsubscribe();
        stateSubject.onCompleted();
        progressHandler = null;
    }

    private void setState(PlayerState state) {
        if (this.state == state)
            return;
        this.state = state;
        stateSubject.onNext(state);
    }

    private void notifyProgress() {
        if (progressSubject.hasObservers())
            progressSubject.onNext(getProgress());
    }

    public PlayerProgress getProgress() {
        if (seeker == null)
            return new PlayerProgress(player.getDuration(), player.getCurrentPosition());
        else
            return new PlayerProgress(seeker.getDuration(), seeker.getCurrent());
    }

    Observable<PlayerProgress> progress() {
        if (progressHandler != null) {
            progressHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressSubject.onNext(getProgress());
                }
            });
        }

        return progressSubject;
    }

    Observable<PlayerState> states() {
        return stateSubject;
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
                    case RELEASE:
                        unholdIfHolding();
                        break;
                    case PULL:
                        pull(((PullEvent)gestureEvent).getDelta());
                        break;
                }
            }
        });
    }
}
