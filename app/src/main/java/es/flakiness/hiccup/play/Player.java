package es.flakiness.hiccup.play;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class Player {
    private final Context context;
    private final MediaPlayer player;
    private final Uri uri;
    private boolean startRequested;
    private PlayerState state;
    private Subscription gestureSubscription;
    private PublishSubject<PlayerState> stateSubject = PublishSubject.create();

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
        setState(PlayerState.PREPARING);
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
        }
    }

    private void unholdIfHolding() {
        if (state == PlayerState.HOLDING)
            start();
    }

    public void release() {
        player.stop();
        player.release();
        gestureSubscription.unsubscribe();
        stateSubject.onCompleted();
    }

    private void setState(PlayerState state) {
        if (this.state == state)
            return;
        this.state = state;
        stateSubject.onNext(state);
    }

    public PlayerProgress getProgress() {
        return new PlayerProgress(player.getDuration(), player.getCurrentPosition());
    }

    Observable<PlayerProgress> intervalProgress(final long intervalMilliseconds) {
        Observable<PlayerProgress> beginning = Observable.create(new Observable.OnSubscribe<PlayerProgress>() {
            @Override
            public void call(final Subscriber<? super PlayerProgress> subscriber) {
                PlayerProgress current = getProgress();
                subscriber.onNext(current);
                long quantized = intervalMilliseconds - (current.getCurrent() % intervalMilliseconds);
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        subscriber.onNext(getProgress());
                    }
                }, quantized);
            }
        });

        // TODO(omo): Should squash same ones.
        return beginning.concatMap(new Func1<PlayerProgress, Observable<? extends PlayerProgress>>() {
            @Override
            public Observable<? extends PlayerProgress> call(PlayerProgress playerProgress) {
                return Observable.interval(intervalMilliseconds, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).map(new Func1<Long, PlayerProgress>() {
                    @Override
                    public PlayerProgress call(Long aLong) {
                        return getProgress();
                    }
                });
            }
        });
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
                    case MAY_UNHOLD:
                        unholdIfHolding();
                        break;
                }
            }
        });
    }
}
