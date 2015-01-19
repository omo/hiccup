package es.flakiness.hiccup.play;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

class PlayingWithSeek implements Playing, Subscription {
    private final Playing playing;
    private final PublishSubject<PlayerProgress> progress = PublishSubject.create();
    private final PublishSubject<PlayerState> states = PublishSubject.create();
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private Subscription seekSubscription;

    public PlayingWithSeek(Playing playing) {
        this.playing = playing;

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

    public void startSeeking(final Seeker seeker, PlayerState nextState) {
        seekSubscription = seeker.currentPositions().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                progress.onNext(new PlayerProgress(seeker.getDuration(), seeker.getCurrent()));
            }
        });

        states.onNext(nextState);
    }

    public void endSeeking() {
        seekSubscription.unsubscribe();
        seekSubscription = null;
    }

    public boolean isSeeking() {
        return null != seekSubscription;
    }

    @Override
    public void unsubscribe() {
        if (seekSubscription != null) {
            seekSubscription.unsubscribe();
            seekSubscription = null;
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
