package es.flakiness.hiccup.play;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class Seeker {

    public static final int UPDATE_INTERVAL = 100;
    public static final int MAX_SEEK_PER_SEC = 10*1000;

    private int duration;
    private int current;
    private float gradient;
    private Subscription intervalSubscription;

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
        float velocity = (gradient * MAX_SEEK_PER_SEC) * (UPDATE_INTERVAL / 1000f);
        current += velocity;
    }

    public int release() {
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
}
