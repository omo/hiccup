package es.flakiness.hiccup.play;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

class Seeker implements Playing, Subscription {
    private final PublishSubject<PlayerProgress> progress = PublishSubject.create();
    private final PublishSubject<PlayerState> states = PublishSubject.create();
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private SeekSession session;
    private Subscription sessionSubscription;

    public Seeker(Playing playing) {

        subscriptions.add(playing.progress().subscribe(new Action1<PlayerProgress>() {
            @Override
            public void call(PlayerProgress playerProgress) {
                if (!isSeeking())
                    progress.onNext(playerProgress);
            }
        }));

        subscriptions.add(playing.states().subscribe(new Action1<PlayerState>() {
            @Override
            public void call(PlayerState playerState) {
                if (!isSeeking())
                    states.onNext(playerState);
            }
        }));
    }

    @Override
    public Observable<PlayerProgress> progress() {
        return this.progress;
    }

    @Override
    public Observable<PlayerState> states() {
        return this.states;
    }

    public void startSeeking(PlayerProgress current, PlayerState nextState) {
        if (isSeeking())
            throw new AssertionError();
        session = new SeekSession(current);
        sessionSubscription = session.currentPositions().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                progress.onNext(new PlayerProgress(session.getDuration(), session.getCurrent()));
            }
        });

        states.onNext(nextState);
    }

    public int endSeeking() {
        sessionSubscription.unsubscribe();
        sessionSubscription = null;
        int position = session.release();
        session = null;
        return position;
    }

    public void setGradient(float gradient) {
        session.setGradient(gradient);
    }

    public boolean isSeeking() {
        return null != sessionSubscription;
    }

    @Override
    public void unsubscribe() {
        if (sessionSubscription != null) {
            sessionSubscription.unsubscribe();
            sessionSubscription = null;
        }

        subscriptions.unsubscribe();
        progress.onCompleted();
        states.onCompleted();
    }

    @Override
    public boolean isUnsubscribed() {
        return subscriptions.isUnsubscribed();
    }
}
