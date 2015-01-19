package es.flakiness.hiccup.play;

import java.io.IOException;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;


public class GestureInterpreter implements Subscription {

    private final String TAG = getClass().getSimpleName();

    final private Player player;
    final private int lastPosition;
    final private Seeker seeker;
    final private CompositeSubscription subscriptions = new CompositeSubscription();

    public GestureInterpreter(Player player, int lastPosition) throws IOException {
        this.player = player;
        this.lastPosition = lastPosition;
        this.seeker = new Seeker(player);
        this.subscriptions.add(this.seeker);
    }

    public Seeker getSeeker() {
        return seeker;
    }

    private void hold() {
        if (player.getState().isHoldable()) {
            seeker.startSeeking(player.getProgress(), PlayerState.HOLDING);
            player.pause();
        }
    }

    private void unholdIfHolding() {
        if (!seeker.isSeeking())
            return;
        int nextPosition = seeker.endSeeking();
        player.seekTo(nextPosition);
        player.start();
    }


    private void flingBack() {
        if (player.getState() != PlayerState.PAUSING)
            return;
        moveToHead();
    }

    private void moveToHead() {
        if (seeker.isSeeking())
            seeker.endSeeking();
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

    public void connectTo(Observable<GestureEvent> gestures) {
        subscriptions.add(gestures.subscribe(new Action1<GestureEvent>() {
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
        }));
    }

    @Override
    public void unsubscribe() {
        subscriptions.unsubscribe();
    }

    @Override
    public boolean isUnsubscribed() {
        return subscriptions.isUnsubscribed();
    }
}
