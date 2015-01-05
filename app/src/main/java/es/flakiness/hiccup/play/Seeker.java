package es.flakiness.hiccup.play;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class Seeker {

    public static final int UPDATE_INTERVAL = 50;
    public static final int MAX_SEEK_PER_SEC = 60*1000;

    private int duration;
    private int current;
    private float gradient;
    private Subscription intervalSubscription;
    private PublishSubject<Integer> currentPositionSubject = PublishSubject.create();

    public Seeker(PlayerProgress progress) {
        this(progress.getDuration(), progress.getCurrent());
    }

    public Seeker(int duration, int current) {
        this.duration = duration;
        this.current = current;
        this.intervalSubscription = Observable.interval(UPDATE_INTERVAL, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                updateCurrent();
            }
        });
    }

    private void updateCurrent() {
        float emphasizedGradient = gradient*gradient*(0 <= gradient ? +1 : -1);
        float velocity = (emphasizedGradient * MAX_SEEK_PER_SEC) * (UPDATE_INTERVAL / 1000f);
        current += velocity;
        if (duration < current)
            current = duration;
        if (current < 0)
            current = 0;
        currentPositionSubject.onNext(current);
    }

    public int release() {
        currentPositionSubject.onCompleted();
        intervalSubscription.unsubscribe();
        return current;
    }

    public void setGradient(float gradient) {
        this.gradient = gradient;
    }

    public int getDuration() {
        return duration;
    }

    public int getCurrent() {
        return current;
    }

    Observable<Integer> currentPositions() {
        return currentPositionSubject;
    }
}
