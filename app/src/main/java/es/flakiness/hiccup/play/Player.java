package es.flakiness.hiccup.play;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class Player {
    private final String TAG = getClass().getSimpleName();

    private final Context context;
    private final MediaPlayer player;
    private final Uri uri;
    private final int lastPosition;
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
            if (progressHandler != null) {
                int delay = UPDATE_INTERVAL - player.getCurrentPosition() % UPDATE_INTERVAL;
                progressHandler.postDelayed(postProgress, delay);
            }
        }
    };

    public Uri getUri() {
        return uri;
    }

    public Player(Context context, Uri uri, int lastPosition) throws IOException {
        this.context = context;
        this.uri = uri;
        this.lastPosition = lastPosition;
        this.player = new MediaPlayer();

        this.player.setDataSource(context, uri);
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

        this.player.prepareAsync();
        setState(PlayerState.PREPARING);

        this.progressHandler = new Handler(Looper.myLooper());
        this.progressHandler.postDelayed(postProgress, 0);

        Toast.makeText(this.context, "last:" + lastPosition, Toast.LENGTH_SHORT).show();
    }

    private void onPlayerSeekComplete() {
        if (state != PlayerState.SEEKING) {
            // This does happen. MediaPlayer calls onPlayerSeekComplete() even when
            // the user doesn't request seekTo().
            return;
        }

        setState(PlayerState.SEEKED);
        startIfNeededAndPossible();
        notifyProgress();
    }

    private void onPlayerCompleted() {
        player.seekTo(player.getDuration() - 1);
        pause();
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
        if (state.isHoldable()) {
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

    private int releaseSeeker() {
        if (null == seeker)
            return 0;
        int position = seeker.release();
        seeker = null;
        return position;
    }

    private void unholdIfHolding() {
        int nextPosition = releaseSeeker();
        if (state == PlayerState.HOLDING) {
            if (1000 < Math.abs(nextPosition - player.getCurrentPosition())) {
                setState(PlayerState.SEEKING);
                this.player.seekTo(nextPosition);
            }

            notifyProgress();
            start();
        }
    }

    private void flingBack() {
        if (state != PlayerState.PAUSING)
            return;
        moveToHead();
    }

    private void moveToHead() {
        releaseSeeker();
        setState(PlayerState.SEEKING);
        this.player.seekTo(0);
        notifyProgress();
        start();
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
                    if (progressHandler != null) {
                        progressSubject.onNext(getProgress());
                    }
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
                    case FLING_BACK:
                        flingBack();
                        break;
                }
            }
        });
    }
}
