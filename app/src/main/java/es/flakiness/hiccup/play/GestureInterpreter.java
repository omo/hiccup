package es.flakiness.hiccup.play;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;


public class GestureInterpreter {
    private final String TAG = getClass().getSimpleName();

    final private Player player;
    final private int lastPosition;
    private Subscription gestureSubscription;
    private Seeker seeker;

    public Uri getUri() {
        return player.getUri();
    }

    public GestureInterpreter(Context context, Uri uri, int lastPosition) throws IOException {
        this.player = new Player(context, uri);
        this.lastPosition = lastPosition;
    }

    private void hold() {
        if (player.getState().isHoldable()) {
            seeker = new Seeker(player.getProgress());
            seeker.currentPositions().subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    player.emit(new PlayerProgress(seeker.getDuration(), seeker.getCurrent()));
                }
            });

            player.hold();
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
        if (player.getState() != PlayerState.HOLDING)
            return;
        player.seekTo(nextPosition);
        player.start();
    }


    private void flingBack() {
        if (player.getState() != PlayerState.PAUSING)
            return;
        moveToHead();
    }

    private void moveToHead() {
        releaseSeeker();
        player.seekTo(0);
        player.start();
    }

    private void toggle() {
        player.toggle();
    }

    private void pull(float gradient) {
        seeker.setGradient(gradient);
    }

    public void start() {
        player.start();
        player.seekTo(lastPosition);
        player.start();
    }

    public void release() {
        player.release();
        gestureSubscription.unsubscribe();
    }

    public Observable<PlayerProgress> progress() {
        return player.progress();
    }

    public Observable<PlayerState> states() {
        return player.states();
    }

    public int getCurrentPosition() {
        return player.getCurrentPosition();
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
