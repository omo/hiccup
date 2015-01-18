package es.flakiness.hiccup.play;

import android.view.Choreographer;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class Seeker {

    public static final int MAX_SEEK_PER_SEC = 60*1000;

    private int duration;
    private int current;
    private float gradient;
    private Subscription intervalSubscription;
    private PublishSubject<Integer> currentPositionSubject = PublishSubject.create();

    private long lastNano = System.nanoTime();
    private Choreographer choreographer;
    // FIXME: Could be abstracted as an Observable.
    private Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            updateCurrent(frameTimeNanos);
        }
    };

    public Seeker(PlayerProgress progress) {
        this(progress.getDuration(), progress.getCurrent());
    }

    public Seeker(int duration, int current) {
        this.duration = duration;
        this.current = current;
        this.choreographer = Choreographer.getInstance();

        updateCurrent(System.nanoTime());
    }

    private void updateCurrent(long currentNano) {
        float delta = (float) (currentNano - lastNano) / 1000000000f;
        lastNano = currentNano;

        float emphasizedGradient = gradient*gradient*(0 <= gradient ? +1 : -1);
        float velocity = (emphasizedGradient * MAX_SEEK_PER_SEC) * delta;
        current += velocity;
        if (duration < current)
            current = duration;
        if (current < 0)
            current = 0;
        currentPositionSubject.onNext(current);
        if (null != choreographer)
            choreographer.postFrameCallback(frameCallback);
    }

    public int release() {
        currentPositionSubject.onCompleted();
        choreographer  = null;
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
